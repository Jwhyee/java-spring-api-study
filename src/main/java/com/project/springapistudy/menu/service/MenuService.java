package com.project.springapistudy.menu.service;

import com.project.springapistudy.common.exception.runtime.DuplicationExceptionRuntime;
import com.project.springapistudy.common.exception.runtime.NotFoundExceptionRuntime;
import com.project.springapistudy.menu.domain.Menu;
import com.project.springapistudy.menu.domain.MenuRepository;
import com.project.springapistudy.menu.object.MenuDto;
import com.project.springapistudy.menu.object.MenuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuVo saveMenu(MenuDto dto) {

        if (existsByMenuName(dto.getMenuName())) {
            throw new DuplicationExceptionRuntime();
        }

        return MenuVo.fromEntity(menuRepository.save(dto.toEntity()));
    }

    @Transactional(readOnly = true)
    public boolean existsByMenuName(String menuName) {
        return menuRepository.existsByMenuName(menuName);
    }

    @Transactional(readOnly = true)
    public MenuVo findById(Long id) {
        Menu currentMenu = findEntityById(id);
        if (currentMenu.getUseYN().equals("Y")) {
            return MenuVo.fromEntity(currentMenu);
        }
        throw new NotFoundExceptionRuntime();
    }

    @Transactional(readOnly = true)
    public List<MenuVo> findAllUsageMenu() {
        List<Menu> list = menuRepository.findAllByUseYN("Y");
        return list.stream()
                .map(MenuVo::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuVo> findAllMenu() {
        List<Menu> list = menuRepository.findAll();
        return list.stream()
                .map(MenuVo::fromEntity)
                .toList();
    }


    @Transactional(readOnly = true)
    public Menu findEntityById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(NotFoundExceptionRuntime::new);
    }

    @Transactional
    public void updateMenu(Long id, MenuDto dto) {
        Menu currentMenu = findEntityById(id);
        currentMenu.updateBasicInfo(dto.getMenuName());
    }

    @Transactional
    public void deleteMenu(Long id) {
        findEntityById(id).remove();
    }
}
