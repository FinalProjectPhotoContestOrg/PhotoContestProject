package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.services.contracts.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/entries")
public class PhotoController {
    private final EntryService entryService;
    private final EntryMapper entryMapper;
    @Autowired
    public PhotoController(EntryService entryService, EntryMapper entryMapper) {
        this.entryService = entryService;
        this.entryMapper = entryMapper;
    }

    @GetMapping("/upload")
    public String getHomeView(Model model) {
        model.addAttribute("entry", new EntryInDto());

        return "UploadPhotoView";
    }

    @PostMapping("/upload")
    public String handlePhotoUpload(@RequestParam("image") MultipartFile file, @ModelAttribute("entry") EntryInDto entryInDto) {

        Entry entry = entryMapper.fromDto(entryInDto);
        try {

            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());


            entry.setPhotoUrl(base64Image);
            entryService.updateEntry(entry);

            return "redirect:/entries/upload";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/entries/upload";
        }
    }

    @GetMapping("/{id}")
    public String getEntryView(@PathVariable int id, Model model) {
        model.addAttribute("entry", entryService.getEntryById(id));
        return "EntryView";
    }
}
