package com.project.springapistudy.menu.service;

import com.project.springapistudy.menu.domain.Menu;
import com.project.springapistudy.menu.domain.MenuRepository;
import com.project.springapistudy.menu.service.MenuService;
import com.project.springapistudy.menu.object.MenuDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 저장 테스트")
    void saveMenuTest() {
        // given
        String menuName = "따뜻한 아이스 아메리라떼";
        MenuDto dto = MenuDto.builder()
                .id(1L)
                .menuName(menuName)
                .build();

        Menu menu = Menu.builder()
                .id(1L)
                .menuName(menuName)
                .build();

        // when
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);
        Menu savedMenu = menuService.saveMenu(dto);

        // then
        assertThat(savedMenu).isNotNull();
        assertThat(savedMenu.getMenuName()).isEqualTo(menuName);
    }

}