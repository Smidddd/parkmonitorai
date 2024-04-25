package sk.umb.parkmonitorai.backend.parklot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.camera.persistence.entity.CameraEntity;
import sk.umb.parkmonitorai.backend.camera.persistence.repository.CameraRepository;
import sk.umb.parkmonitorai.backend.occupancy.service.OccupancyCache;
import sk.umb.parkmonitorai.backend.parklot.persistence.entity.ParklotEntity;
import sk.umb.parkmonitorai.backend.parklot.persistence.repository.ParklotRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ParklotService {
    private final ParklotRepository parklotRepository;
    private final CameraRepository cameraRepository;

    public ParklotService(ParklotRepository parklotRepository, CameraRepository cameraRepository){
        this.parklotRepository = parklotRepository;
        this.cameraRepository = cameraRepository;
    }

    private List<ParklotDetailDTO> mapToDto(List<ParklotEntity> parklotEntities) {
        List<ParklotDetailDTO> dtos = new ArrayList<>();

        for (ParklotEntity pe : parklotEntities) {
            ParklotDetailDTO dto = new ParklotDetailDTO();

            dto.setId(pe.getId());
            dto.setGeometry(pe.getGeometry());
            dto.setLatitude(pe.getLatitude());
            dto.setLongitude(pe.getLongitude());
            dto.setCamera(pe.getCamera());

            dtos.add(dto);
        }

        return dtos;
    }

    private ParklotDetailDTO mapToDto(ParklotEntity parklotEntity) {
        ParklotDetailDTO dto = new ParklotDetailDTO();

        dto.setId(parklotEntity.getId());
        dto.setCamera(parklotEntity.getCamera());
        dto.setGeometry(parklotEntity.getGeometry());
        dto.setLongitude(parklotEntity.getLongitude());
        dto.setLatitude(parklotEntity.getLatitude());

        return dto;
    }
    private ParklotEntity mapToEntity(ParklotRequestDTO dto) {
        ParklotEntity pe = new ParklotEntity();

        pe.setCamera(dto.getCamera());
        pe.setLatitude(dto.getLatitude());
        pe.setLongitude(dto.getLongitude());
        pe.setGeometry(dto.getGeometry());

        return pe;
    }

    public List<ParklotDetailDTO> getAllParklots(){
        return mapToDto(parklotRepository.findAll());
    }
    public List<ParklotDetailDTO> getAllParklotsForCamera(Long cameraId){
        return mapToDto(parklotRepository.findParklotsByCameraId(cameraRepository.findById(cameraId).get()));
    }

    public ParklotDetailDTO getParklotById(Long cameraId){
        return mapToDto(parklotRepository.findById(cameraId).get());
    }

    public Long createParklot(ParklotRequestDTO parklotRequestDTO){
        return parklotRepository.save(mapToEntity(parklotRequestDTO)).getId();
    }
    public int createParklots(ParklotRequestDTO[] parklotRequestDTOS){
        for (int i=0; i<parklotRequestDTOS.length; i++){
            parklotRepository.save(mapToEntity(parklotRequestDTOS[i]));
        }
        return parklotRequestDTOS.length;
    }

    public void updateParklot(Long parklotId, ParklotRequestDTO parklotRequestDTO) {
        ParklotEntity parklotEntity = parklotRepository.findById(parklotId).get();

        if (parklotRequestDTO.getGeometry().size() != 0) {
            parklotEntity.setGeometry(parklotRequestDTO.getGeometry());
        }
        if (parklotRequestDTO.getLatitude() != null) {
            parklotEntity.setLatitude(parklotRequestDTO.getLatitude());
        }
        if (parklotRequestDTO.getLongitude() != null) {
            parklotEntity.setLongitude(parklotRequestDTO.getLongitude());
        }
        if (parklotRequestDTO.getCamera() != null) {
            parklotEntity.setCamera(parklotRequestDTO.getCamera());
        }

        parklotRepository.save(parklotEntity);
    }

    public void deleteParklot(Long parklotId){
        parklotRepository.deleteById(parklotId);
    }


    @Autowired
    private TaskScheduler taskScheduler;

    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private Map<Long, Boolean> taskEnabled = new ConcurrentHashMap<>();

    // Start scheduling tasks for specific camera
    public synchronized int startTask(Long cameraId) {
        CameraEntity camera = this.cameraRepository.findById(cameraId).get();
        if (taskEnabled.getOrDefault(cameraId, false)) {
            // Task already scheduled
            return -1;
        }

        taskEnabled.put(cameraId, true);

        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                cutImage(camera);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, new CronTrigger("0 */1 * * * *"));

        scheduledTasks.put(cameraId, future);

        System.out.println("Scheduled task for camera: " + cameraId);
        return 1; // Task successfully scheduled
    }

    // Stop scheduling tasks for specific camera
    public synchronized int stopTask(Long cameraId) {
        if (!taskEnabled.containsKey(cameraId) || !taskEnabled.get(cameraId)) {
            // Task not scheduled
            return -1;
        }

        ScheduledFuture<?> future = scheduledTasks.remove(cameraId);
        if (future != null) {
            future.cancel(true);
        }

        taskEnabled.put(cameraId, false);
        System.out.println("Task stopped for camera: " + cameraId);
        return Math.toIntExact(cameraId); // Task successfully stopped
    }



    private static File convertByteArrayToImage(byte[] imageData) {
        try {
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageData));
            File tempFile = File.createTempFile("temp_image", ".jpg"); // Create a temporary file
            ImageIO.write(bImage, "jpg", tempFile); // Write the image data to the temporary file
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void cutImage(CameraEntity camera) throws IOException {
        List<ParklotEntity> parklots = this.parklotRepository.findParklotsByCameraId(camera);
        //get image from camera api
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(camera.getSource()))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<byte[]> response = null;
        File imageFile = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
            // Assuming response is successful (status code 200)
            if (response.statusCode() == 200) {
                byte[] imageData = response.body();
                imageFile = convertByteArrayToImage(imageData);
                System.out.println("Image downloaded successfully. File: " + imageFile.getAbsolutePath());
            } else {
                System.out.println("Failed to download image. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        File directory = new File("C:\\Users\\smido\\Desktop\\Bc. Praca\\parkmonitorai\\backend\\src\\main\\resources\\static\\camera_" + camera.getId());
        directory.mkdirs(); // Automatically creates the directory if it doesn't exist

        //image processing
        BufferedImage source = ImageIO.read(imageFile);

        for (ParklotEntity parklot : parklots) {
            GeneralPath clip = new GeneralPath();
            clip.moveTo(parklot.getGeometry().get(0).getX(), parklot.getGeometry().get(0).getY());
            clip.lineTo(parklot.getGeometry().get(1).getX(), parklot.getGeometry().get(1).getY());
            clip.lineTo(parklot.getGeometry().get(2).getX(), parklot.getGeometry().get(2).getY());
            clip.lineTo(parklot.getGeometry().get(3).getX(), parklot.getGeometry().get(3).getY());
            clip.closePath();

            Rectangle bounds = clip.getBounds();
            BufferedImage img = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            clip.transform(AffineTransform.getTranslateInstance(-pointWithLowestX(parklot.getGeometry()).getX(), -pointWithLowestY(parklot.getGeometry()).getY()));
            g2d.setClip(clip);
            g2d.translate(-pointWithLowestX(parklot.getGeometry()).getX(), -pointWithLowestY(parklot.getGeometry()).getY());
            g2d.drawImage(source, 0, 0, null);
            g2d.dispose();

            File imageeFile = new File(directory, parklot.getId() + ".png");
            ImageIO.write(img, "png", imageeFile);
        }
        System.out.println("Task executed at: " + new java.util.Date());
    }
    public static Point pointWithLowestY(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        Point minPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            if (currentPoint.getY() < minPoint.getY()) {
                minPoint = currentPoint;
            }
        }

        return minPoint;
    }
    public static Point pointWithLowestX(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        Point minPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            if (currentPoint.getX() < minPoint.getX()) {
                minPoint = currentPoint;
            }
        }

        return minPoint;
    }

}
