#!.venv/bin/python3

import sys
import os

import torch
import torch.nn as nn
import torch.nn.functional as F

import torchvision
from torchvision import transforms
from torchvision.datasets import ImageFolder

INPUT_WIDTH=32#128
INPUT_HEIGHT=32#128

if len(sys.argv) < 2:
    print('zadaj nazov obrazku!')
    sys.exit(1)

# vytvor siet
class Net(nn.Module):
    def __init__(self):
        super().__init__()                  # 32x32
        self.conv1 = nn.Conv2d(3, 6, 5)     # 28x28
        self.pool = nn.MaxPool2d(2, 2)      # 14x14
        self.conv2 = nn.Conv2d(6, 16, 5)    # 10x10
        self.fc1 = nn.Linear(16 * 5 * 5, 120)
        self.fc2 = nn.Linear(120, 84)
        self.fc3 = nn.Linear(84, 2)

    def forward(self, x):
        x = self.pool(F.relu(self.conv1(x)))
        x = self.pool(F.relu(self.conv2(x)))
        x = torch.flatten(x, 1) # flatten all dimensions except batch
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = self.fc3(x)
        return x


net = Net()

net = Net()
PATH = './cifar_net.pth'
net.load_state_dict(torch.load(PATH))

def zobraz_vysledok(dirList, outputs):
    for i in range(len(outputs)):
        output = outputs[i]
        # Remove the extension from the filename
        filename_without_extension = dirList[i].split('.')[0]
        print(filename_without_extension, end=" ")
        if output[0] > output[1]:
            print('true')  # volne
        else:
            print('false')  # obsadene

    #_, predicted = torch.max(outputs, 1)


# 1) jeden obrazok
#image = torchvision.io.read_image(sys.argv[1])[:3]       # odsekni alpha kanal
#resize = transforms.Resize(size=(INPUT_HEIGHT, INPUT_WIDTH))
#image = resize(image)
#image = image.unsqueeze(0)      # pridaj 0. dimenziu (batch)
#image = image.float()
#image /= 255
#outputs = net(image)
#zobraz_vysledok([sys.argv[1]], [outputs])

# 2) cely adresar
resize = transforms.Resize(size=(INPUT_HEIGHT, INPUT_WIDTH))
images = []
dirList = os.listdir(sys.argv[1])
for f in dirList:
    image = torchvision.io.read_image(os.path.join(sys.argv[1], f))[:3]       # odsekni alpha kanal
    image = resize(image)
    image = image.float()
    image /= 255
    images.append(image)

images = torch.stack(images)
outputs = net(images)
zobraz_vysledok(dirList, outputs)


