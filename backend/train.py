#!.venv/bin/python3

# https://pytorch.org/tutorials/beginner/blitz/cifar10_tutorial.html
# https://pyimagesearch.com/2021/10/04/image-data-loaders-in-pytorch/

import os
import sys
from imutils import paths
import numpy as np
import shutil

import torchvision
#import torchvision.transforms as transforms
from torchvision import transforms
from torchvision.datasets import ImageFolder
from torch.utils.data import DataLoader

import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim


RAW_IMAGES='./images-raw'
TRAIN_IMAGES='./images-train'
VALID_IMAGES='./images-valid'
VAL_SPLIT=0.1
INPUT_WIDTH=32#128
INPUT_HEIGHT=32#128
BATCH_SIZE=8

#TODO brat to po mesiacoch; ze napr. trenovacie z januara-marec, validacne z aprila, atd.
#TODO resnet (residual net/connections)
#TODO https://www.linkedin.com/advice/3/what-best-practices-preventing-vanishing-bmaef

# rozdel data na trenovacie a validacne
imagePaths = list(paths.list_images(RAW_IMAGES))
np.random.shuffle(imagePaths)

valPathsLen = int(len(imagePaths) * VAL_SPLIT)
trainPathsLen = len(imagePaths) - valPathsLen
trainPaths = imagePaths[:trainPathsLen]
valPaths = imagePaths[trainPathsLen:]

def copy_images(imagePaths, folder):
	# check if the destination folder exists and if not create it
	if not os.path.exists(folder):
		os.makedirs(folder)
	# loop over the image paths
	for path in imagePaths:
		# grab image name and its label from the path and create
		# a placeholder corresponding to the separate label folder
		imageName = path.split(os.path.sep)[-1]
		label = path.split(os.path.sep)[-2]
		labelFolder = os.path.join(folder, label)
		# check to see if the label folder exists and if not create it
		if not os.path.exists(labelFolder):
			os.makedirs(labelFolder)
		# construct the destination image path and copy the current
		# image to it
		destination = os.path.join(labelFolder, imageName)
		shutil.copy(path, destination)

shutil.rmtree(TRAIN_IMAGES)
copy_images(trainPaths, TRAIN_IMAGES)
shutil.rmtree(VALID_IMAGES)
copy_images(valPaths, VALID_IMAGES)

# data augmentation
resize = transforms.Resize(size=(INPUT_HEIGHT, INPUT_WIDTH))
hFlip = transforms.RandomHorizontalFlip(p=0.25)
vFlip = transforms.RandomVerticalFlip(p=0.25)
rotate = transforms.RandomRotation(degrees=15)

trainTransforms = transforms.Compose([resize, hFlip, vFlip, rotate, transforms.ToTensor()])
valTransforms = transforms.Compose([resize, transforms.ToTensor()])
# okrem transformacii to skonvertuje na tenzory (okrem ineho z 0..255 na 0..1)

# priprav pytorch datasets a dataloaders
trainDataset = ImageFolder(root=TRAIN_IMAGES, transform=trainTransforms)
#print(trainDataset.classes)
valDataset = ImageFolder(root=VALID_IMAGES, transform=valTransforms)
print("velkost trenovacieho datasetu: {}".format(len(trainDataset)))
print("velkost validacneho datasetu: {}".format(len(valDataset)))

trainDataLoader = DataLoader(trainDataset, batch_size=BATCH_SIZE, shuffle=True)
valDataLoader = DataLoader(valDataset, batch_size=BATCH_SIZE)

# vytvor siet
class Net(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(3, 6, 5)     # 3x6x5x5 hodnot
        self.pool = nn.MaxPool2d(2, 2)
        self.conv2 = nn.Conv2d(6, 16, 5)
        self.fc1 = nn.Linear(16 * 5 * 5, 120)
        self.fc2 = nn.Linear(120, 84)
        self.fc3 = nn.Linear(84, 2)

    def forward(self, x):
        #print(x.size())                        # 32x32
        x = self.pool(F.relu(self.conv1(x)))    # 28x28 -> 14x14
        x = self.pool(F.relu(self.conv2(x)))    # 10x10 -> 5x5
        x = torch.flatten(x, 1) # flatten all dimensions except batch: 8x400
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = self.fc3(x)
        return x


net = Net()

#device = torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
#net.to(device)
#inputs, labels = data[0].to(device), data[1].to(device)

# definuj loss function a optimizer
criterion = nn.CrossEntropyLoss()
#optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)
optimizer = optim.Adam(net.parameters(), lr=0.001)

# trenuj siet
for epoch in range(10):  # loop over the dataset multiple times

    running_loss = 0.0
    for i, data in enumerate(trainDataLoader, 0):
        # get the inputs; data is a list of [inputs, labels]
        inputs, labels = data

        # zero the parameter gradients
        optimizer.zero_grad()
        #print(net.conv2.weight)

        # forward + backward + optimize
        print('aaaaaaaaaaaaaaaa')
        print(inputs.shape)
        outputs = net(inputs)
        loss = criterion(outputs, labels)       # [ [-0.5, 0.5], ... 8x ] , [ [1], ... 8x ]
        loss.backward()
        #print(net.conv1.weight.grad)
        optimizer.step()

        # print statistics
        running_loss += loss.item()
        if i % 100 == 0:
            print(f'[{epoch + 1}, {i + 1:5d}] loss: {running_loss / 2000:.3f}')
            running_loss = 0.0

print('Finished Training')

PATH = './cifar_net.pth'
torch.save(net.state_dict(), PATH)


# test
correct = 0
total = 0
# since we're not training, we don't need to calculate the gradients for our outputs
with torch.no_grad():
    for data in valDataLoader:
        images, labels = data       # torch.Size([8, 3, 32, 32])
        # calculate outputs by running images through the network
        outputs = net(images)
        # the class with the highest energy is what we choose as prediction
        _, predicted = torch.max(outputs.data, 1)
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

print(f'Accuracy of the network on the {total} test images: {100 * correct // total} %')



