package com.project.springapistudy.menu.controller;

import com.project.springapistudy.menu.object.MenuDto;
import com.project.springapistudy.menu.object.MenuVo;
import com.project.springapistudy.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuRestController {

    private final MenuService service;

    @PostMapping
    public ResponseEntity<MenuVo> saveMenuApi(@RequestBody @Valid MenuDto dto) {
        MenuVo menuVo = service.saveMenu(dto);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{menuId}")
                                .buildAndExpand(menuVo.getId())
                                .toUri())
                .build();
    }

    @GetMapping
    public ResponseEntity<List<MenuVo>> getAllUsageMenuApi() {
        return ResponseEntity.ok(service.findAllUsageMenu());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<MenuVo>> getAllMenuApi() {
        return ResponseEntity.ok(service.findAllMenu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuVo> getMenuByIdApi(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMenuByIdApi(@PathVariable Long id, @RequestBody @Valid MenuDto dto) {
        service.updateMenu(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMenuByIdApi(@PathVariable Long id) {
        service.deleteMenu(id);
    }
}
