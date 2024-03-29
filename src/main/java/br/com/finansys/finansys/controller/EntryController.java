package br.com.finansys.finansys.controller;

import br.com.finansys.finansys.dto.CategoryDTO;
import br.com.finansys.finansys.dto.EntryDTO;
import br.com.finansys.finansys.entity.Category;
import br.com.finansys.finansys.entity.Entry;
import br.com.finansys.finansys.service.CategoryService;
import br.com.finansys.finansys.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/entry")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    private final CategoryService categoryService;

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public EntryDTO createEntry(@RequestBody @Valid EntryDTO entryDTO) {
        Category category = categoryService.getCategory(entryDTO.getCategoryId(), entryDTO.getUserId());
        Entry entry = entryService.createEntry(entryDTO, category);
        entryDTO.setId(entry.getId());
        entryDTO.setUserId(entry.getUserId());
        entryDTO.setDate(entry.getDate().toString());
        entryDTO.setCategory(CategoryDTO.builder()
                .id(category.getId())
                .userId(category.getUserId())
                .name(category.getName())
                .description(category.getDescription())
                .build());
        return entryDTO;
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntryDTO getEntry(@PathVariable Integer id, @RequestParam Integer userId) {
        Entry entry = entryService.getEntry(id, userId);
        return EntryDTO.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .name(entry.getName())
                .description(entry.getDescription())
                .type(entry.getType())
                .amount(entry.getAmount().toString().replace(".", ","))
                .date(entry.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .paid(entry.getPaid())
                .categoryId(entry.getCategory().getId())
                .category(CategoryDTO.builder()
                        .id(entry.getCategory().getId())
                        .userId(entry.getCategory().getUserId())
                        .name(entry.getCategory().getName())
                        .description(entry.getCategory().getDescription())
                        .build())
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EntryDTO> getAllEntries(@RequestParam Integer userId) {
        List<Entry> entryList = entryService.getAllEntries(userId);
        List<EntryDTO> entryDTOList = new ArrayList<>();
        entryList.forEach(entry -> {
            EntryDTO entryDTO = EntryDTO.builder()
                    .id(entry.getId())
                    .userId(entry.getUserId())
                    .name(entry.getName())
                    .description(entry.getDescription())
                    .type(entry.getType())
                    .amount(entry.getAmount().toString().replace(".", ","))
                    .date(entry.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .paid(entry.getPaid())
                    .categoryId(entry.getCategory().getId())
                    .category(CategoryDTO.builder()
                            .id(entry.getCategory().getId())
                            .userId(entry.getCategory().getUserId())
                            .name(entry.getCategory().getName())
                            .description(entry.getCategory().getDescription())
                            .build())
                    .build();
            entryDTOList.add(entryDTO);
        });
        return entryDTOList;
    }

    @Transactional
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEntry(@PathVariable Integer id, @RequestBody @Valid EntryDTO entryDTO) {
        Integer userId = entryDTO.getUserId();
        Category category = categoryService.getCategory(entryDTO.getCategoryId(), userId);
        entryService.updateEntry(id, entryDTO, category, userId);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Integer id, @RequestParam Integer userId) {
        entryService.deleteEntry(id, userId);
    }
}
