package gobbler.controllers;

import gobbler.domain.Gobbler;
import gobbler.repositories.GobblerRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @GetMapping("/search/{name}")
    public String search(@PathVariable String name, Model model) {

        List<Gobbler> gobblers = new ArrayList<>();

        model.addAttribute("gobblers", gobblers);
        return "search";
    }

    @GetMapping("/search")
    public String search2(@RequestParam String search) {
        return "redirect:/search/" + search;
    }

}
