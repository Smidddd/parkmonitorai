package sk.umb.parkmonitorai.backend.occupancy.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import sk.umb.parkmonitorai.backend.camera.persistence.repository.CameraRepository;
import sk.umb.parkmonitorai.backend.occupancy.persistence.entity.OccupancyCacheEntity;
import sk.umb.parkmonitorai.backend.occupancy.persistence.repository.OccupancyRepository;
import sk.umb.parkmonitorai.backend.parklot.persistence.repository.ParklotRepository;
import sk.umb.parkmonitorai.backend.parklot.service.ParklotService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class OccupancyService {
    private final ParklotRepository parklotRepository;
    private final CameraRepository cameraRepository;
    private final ParklotService parklotService;
    private final OccupancyRepository occupancyRepository;

    public OccupancyService(ParklotRepository parklotRepository, CameraRepository cameraRepository, ParklotService parklotService, OccupancyRepository occupancyRepository){
        this.parklotRepository = parklotRepository;
        this.cameraRepository = cameraRepository;
        this.parklotService = parklotService;
        this.occupancyRepository = occupancyRepository;
    }

    public List<OccupancyCache> getCurrentOccupancy(Long cameraId) throws IOException {
        //Vycistit adresar a naplnit novymi sektormi
        File directory = new File("C:\\Users\\smido\\Desktop\\Bc. Praca\\parkmonitorai\\backend\\src\\main\\resources\\static\\camera_" + cameraId);
        FileUtils.cleanDirectory(directory);
        parklotService.cutImage(cameraRepository.findById(cameraId).get());
        //Ak je prazdna cache v DB
//        if (occupancyRepository.findOCEByCameraId(cameraId).size() > 0){
//            //Ak su zaznamy novsie ako 3 minuty
//            List<OccupancyCacheEntity> current = occupancyRepository.findAll();
//            if (Duration.between(current.get(0).getSaved_at().toInstant(), new Timestamp(System.currentTimeMillis()).toInstant()).abs().toMinutes() < 3){
//                //Konvertovanie pre poslanie na frontend
//                List<OccupancyCache> occupancy = new ArrayList<>();
//                for (OccupancyCacheEntity oce : current) {
//                    OccupancyCache parklot = new OccupancyCache();
//                    parklot.setOccupancy(oce.isOccupancy());
//                    parklot.setParklot(oce.getParklot());
//                    occupancy.add(parklot);
//                }
//                return occupancy;
//            }
//        }
        //Analyza pomocou NN
        File root = new File("C:\\Users\\smido\\Desktop\\Bc. Praca\\parkmonitorai\\backend");
        List<OccupancyCache> occupancy = new ArrayList<>();
        List<OccupancyCacheEntity> OCEs = new ArrayList<>();
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"python", "analyze.py", "src/main/resources/static/camera_"+cameraId};
        Process proc = rt.exec(commands, null, root);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            String[] split = s.split(" ");
            Long parklotId =(long) Integer.parseInt(split[0]);
            boolean free = Boolean.parseBoolean(split[1]);

            OccupancyCacheEntity oce = new OccupancyCacheEntity();
            oce.setOccupancy(free);
            oce.setParklot(parklotRepository.findById(parklotId).get());
            oce.setCamera_id(cameraId);
            OCEs.add(oce);
        }

        // Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        //Ulozit zaznamy do DB
        occupancyRepository.deleteAll();
        occupancyRepository.saveAll(OCEs);
        //Konvertovanie na frontend
        for (OccupancyCacheEntity oce : OCEs) {
            OccupancyCache parklot = new OccupancyCache();
            parklot.setOccupancy(oce.isOccupancy());
            parklot.setParklot(oce.getParklot());
            occupancy.add(parklot);
        }
        return occupancy;
    }
}
