package com.project.springapistudy.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.springapistudy.common.exception.runtime.NotFoundException;
import com.project.springapistudy.menu.domain.Menu;
import com.project.springapistudy.menu.domain.MenuRepository;
import com.project.springapistudy.menu.object.MenuVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MenuRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MenuRepository menuRepository;

    private String baseUrl = "/menu";
    private final String menuName = "따뜻한 아이스 라떼";


    private final String req = """
            {
                "menuName" : "%s",
                "useYN" : "%s"
            }
            """;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @BeforeEach
    void deleteAll() {
        menuRepository.deleteAll();
    }

    @Nested
    @DisplayName("메뉴 저장 요청")
    class CreateMenuApiTest {

        @Test
        @DisplayName("메뉴 저장 요청 성공")
        void postApiReqSuccess() throws Exception {


            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                            .content(req.formatted("미지근한 카라멜 라떼", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();

        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 메뉴 이름 누락")
        void postApiReqInvalidByMenuName() throws Exception {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                            .content(req.formatted("", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();
        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 사용 여부 누락")
        void postApiReqInvalidByUseYN() throws Exception {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                            .content(req.formatted("딸기 아이스 커피", ""))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();
        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 모든 필드 누락")
        void postApiReqInvalid() throws Exception {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                            .content(req.formatted("", ""))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();
        }
    }

    @Nested
    @DisplayName("메뉴 검색 요청")
    class FindMenuApiTest {

        @Test
        @DisplayName("메뉴 검색 성공")
        void getApiReqSuccess() throws Exception {
            final String resultUrl = saveMenu("따뜻한 오렌지 주스");
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(resultUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            MenuVo menuVo = parseObject(mvcResult, MenuVo.class);


            assertThat(menuVo.getMenuName()).isEqualTo("따뜻한 오렌지 주스");

        }

        @Test
        @DisplayName("메뉴 검색 성공 - 삭제 메뉴를 제외한 전체 메뉴")
        void getApiReqSuccessByUsableMenu() throws Exception {
            final String resultUrl = saveMenu("맛동산 에이드");
            saveMenu("김치 라떼");
            saveMenu("오징어 호두 에이드");

            mockMvc.perform(MockMvcRequestBuilders.delete(resultUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            ArrayList arrayList = parseObject(mvcResult, ArrayList.class);

            assertThat(arrayList.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("메뉴 검색 성공 - 삭제를 포함한 전체 메뉴 조회")
        void getApiReqSuccessByAllMenu() throws Exception {
            final String resultUrl = saveMenu("오징어 커피 땅콩 에이드");
            saveMenu("김치찌개 라떼");
            saveMenu("순두부찌개 에이드");

            mockMvc.perform(MockMvcRequestBuilders.delete(resultUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/admin")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            ArrayList arrayList = parseObject(mvcResult, ArrayList.class);

            assertThat(arrayList.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("메뉴 검색 실패 - 존재하지 않는 ID")
        void getApiReqFailById() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/9827348")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

    }

    @Nested
    @DisplayName("메뉴 수정 요청")
    class UpdateMenuApiTest {

        @Test
        @DisplayName("메뉴 수정 성공")
        void putApiReqSuccess() throws Exception {
            final String resultUrl = saveMenu("뜨거운 딸기 팥빙수");
            mockMvc.perform(MockMvcRequestBuilders.put(resultUrl)
                            .content(req.formatted("뜨거운 딸기 팥빙수에 두리안 추가", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            MenuVo menuVo = parseObject(findByMenu(resultUrl), MenuVo.class);

            assertThat(menuVo.getMenuName()).isEqualTo("뜨거운 딸기 팥빙수에 두리안 추가");

        }

        @Test
        @DisplayName("메뉴 수정 실패 - 메뉴 이름 누락")
        void putApiReqInvalid() throws Exception {
            final String resultUrl = saveMenu(menuName);
            mockMvc.perform(MockMvcRequestBuilders.put(resultUrl)
                            .content(req.formatted("", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("메뉴 삭제 요청")
    class DeleteMenuApiTest {

        @Test
        @DisplayName("메뉴 삭제 성공 - id로 조회 불가")
        void deleteApiReqSuccessAndFindById() throws Exception {
            final String requestUrl = saveMenu("호박죽에 팥 추가");

            mockMvc.perform(MockMvcRequestBuilders.delete(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            mockMvc.perform(MockMvcRequestBuilders.get(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("메뉴 삭제 성공 - DB에 데이터 존재 확인")
        void deleteApiReqSuccessAndExistsDatabase() throws Exception {
            final String requestUrl = saveMenu("팥죽에 호박 추가");


            mockMvc.perform(MockMvcRequestBuilders.delete(requestUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            String[] split = requestUrl.split("/");

            Menu findMenu = findByMenuId(Long.valueOf(split[split.length - 1]));

            assertSoftly(softAssertions -> {
                softAssertions.assertThat(findMenu.getMenuName()).isEqualTo("팥죽에 호박 추가");
                softAssertions.assertThat(findMenu.getUseYN()).isEqualTo("N");
            });
        }

        @Test
        @DisplayName("메뉴 삭제 실패 - 존재하지 않는 번호")
        void deleteApiReqFailById() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/9999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }


    private MvcResult findByMenu(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }


    private String saveMenu(String menuName) throws Exception {
        String req = """
                {
                    "menuName" : "%s",
                    "useYN" : "%s"
                }
                """.formatted(menuName, "Y");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                        .content(req)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();



        return mvcResult.getResponse().getRedirectedUrl();
    }

    private <T> T parseObject(MvcResult mvcResult, Class<T> type) throws Exception {
        return mapper.readValue(mvcResult.getResponse().getContentAsString(), type);
    }

    private Menu findByMenuId(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }


}