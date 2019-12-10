package gobbler.controllers;

import gobbler.services.NewGobblerService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NewGobblerController {

    @Autowired
    private NewGobblerService newGobblerService;

    @RequestMapping("/newGobbler")
    public String newGobbler() {
        return "newGobbler";
    }

    @PostMapping("/newGobbler")
    public String createNewGobbler(@RequestParam String gobblerName, @RequestParam String name,
            @RequestParam String password, @RequestParam String password2,
            @RequestParam String gobblerPath, Model model) throws IOException {

        List<String> messages = newGobblerService.newGobbler(gobblerName, name, password, password2, gobblerPath);

        if (messages.isEmpty()) {
            messages.add("new Gobbler created!");
            model.addAttribute("messages", messages);
            return "/newGobbler";
        } else {
            model.addAttribute("messages", messages);
            return "/newGobbler";
        }
    }
}
