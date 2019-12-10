package gobbler.controllers;

import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class GobblersController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private GobbleRepository gobbleRepository;

    @RequestMapping("/gobblers/{gobblerPath}")
    public String gobblers(@PathVariable String gobblerPath, Model model) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {
            model.addAttribute("gobbler", loggedGobbler);
            return "ownProfile";
        } else {
            model.addAttribute("loggedGobbler", loggedGobbler);
            model.addAttribute("searchedGobbler", searchedGobbler);
            return "gobblers";
        }
    }

    @PostMapping("/gobblers/{gobblerPath}/addPicture")
    public String addPhoto(@PathVariable String gobblerPath, Model model,
            @RequestParam("info") String info,
            @RequestParam("picture") MultipartFile file) throws IOException {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

            String[] type = file.getContentType().split("/");

            if (type[0].equals("image")) {
                Picture picture = new Picture();
                picture.setContent(file.getBytes());
                if (info == null || info.trim().equals("")) {
                    picture.setInfo("No info");
                } else {
                    if (info.length() > 100) {
                        info = info.substring(0, 100);
                    }
                    picture.setInfo(info);
                }
                if (loggedGobbler.addPicture(picture)) {
                    pictureRepository.save(picture);
                    gobblerRepository.save(loggedGobbler);
                } else {
                    model.addAttribute("message", "Your album is full!");
                }
            } else {
                model.addAttribute("message", "File was not a picture");
            }

        }

        return "redirect:/gobblers/{gobblerPath}";
    }

    @GetMapping(path = "/gobblers/{gobblerPath}/images/{id}/content", produces = "image/png")
    @ResponseBody
    public byte[] imageContent(@PathVariable Long id, @PathVariable String gobblerPath) {
        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return null;
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {
            Picture picture = pictureRepository.getOne(id);
            if (loggedGobbler.getAlbum().contains(picture)) {
                return picture.getContent();
            }
        }
        return null;
    }

    @DeleteMapping("/gobblers/{gobblerPath}/delPicture/{id}")
    public String removePicture(@PathVariable String gobblerPath,
            @PathVariable Long id, Model model) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

            if (pictureRepository.getOne(id) == null) {
                return "redirect:/gobblers/{gobblerPath}";
            }
            Picture picture = pictureRepository.getOne(id);
            if (!picture.equals(loggedGobbler.getProfilePicture())) {
                loggedGobbler.removePicture(picture);
                pictureRepository.delete(picture);
                gobblerRepository.save(loggedGobbler);
            }
        }

        return "redirect:/gobblers/{gobblerPath}";
    }

    @PostMapping("/gobblers/{gobblerPath}/gobbles")
    public String gobble(@PathVariable String gobblerPath, Model model,
            @RequestParam("gobble") String gobbleContent) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/feed";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

            if (gobbleContent == null || gobbleContent.trim().equals("")) {
                return "redirect:/feed";
            }

            if (gobbleContent.length() > 300) {
                gobbleContent = gobbleContent.substring(0, 300);
            }
            Gobble gobble = new Gobble();
            gobble.setContent(gobbleContent);
            gobble.setTime(LocalDateTime.now());

            loggedGobbler.addGobble(gobble);
            
            gobbleRepository.save(gobble);
            gobblerRepository.save(loggedGobbler);
        }

        return "redirect:/feed";
    }

}
