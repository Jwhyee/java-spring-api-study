package com.project.springapistudy.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.springapistudy.common.handler.ResponseData;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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

    @Nested
    @DisplayName("메뉴 저장 요청")
    class CreateMenuApiTest {

        @Test
        @DisplayName("메뉴 저장 요청 성공")
        void postApiReqSuccess() throws Exception {


            MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                            .content(req.formatted("미지근한 카라멜 라떼", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();

        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 메뉴 이름 누락")
        void postApiReqInvalidByMenuName() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                            .content(req.formatted("", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();
        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 사용 여부 누락")
        void postApiReqInvalidByUseYN() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                            .content(req.formatted("딸기 아이스 커피", ""))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResponse()).isNotNull();
        }

        @Test
        @DisplayName("메뉴 저장 요청 실패 - 모든 필드 누락")
        void postApiReqInvalid() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post(baseUrl)
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
            MvcResult mvcResult = mockMvc.perform(get(resultUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            MenuVo menuVo = parseObject(mvcResult, MenuVo.class);


            assertThat(menuVo.getMenuName()).isEqualTo("따뜻한 오렌지 주스");

        }

        @Test
        @DisplayName("메뉴 검색 실패 - 존재하지 않는 ID")
        void getApiReqFailById() throws Exception {
            mockMvc.perform(get(baseUrl + "/9827348")
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
            mockMvc.perform(put(resultUrl)
                            .content(req.formatted("뜨거운 딸기 팥빙수에 두리안 추가", "Y"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            MenuVo menuVo = parseObject(findByMenu(resultUrl), MenuVo.class);

            assertThat(menuVo.getMenuName()).isEqualTo("뜨거운 딸기 팥빙수에 두리안 추가");

        }

        @Test
        @DisplayName("메뉴 수정 실패 - 유효성 검증 실패")
        void putApiReqInvalid() throws Exception {
            final String resultUrl = saveMenu(menuName);
            mockMvc.perform(put(resultUrl)
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
        @DisplayName("메뉴 삭제 성공")
        void deleteApiReqSuccess() throws Exception {
            final String resultUrl = saveMenu("아이스 호박죽");
            mockMvc.perform(delete(resultUrl)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            MenuVo menuVo = parseObject(findByMenu(resultUrl), MenuVo.class);

            assertThat(menuVo.getUseYN()).isEqualTo("N");
        }

        @Test
        @DisplayName("메뉴 삭제 실패")
        void deleteApiReqFailById() throws Exception {
            mockMvc.perform(delete(baseUrl + "/9999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }


    private MvcResult findByMenu(String url) throws Exception {
        return mockMvc.perform(get(url)
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

        MvcResult mvcResult = mockMvc.perform(post(baseUrl)
                        .content(req)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();



        return mvcResult.getResponse().getRedirectedUrl();
    }

    private <T> T parseObject(MvcResult mvcResult, Class<T> type) throws Exception {
        return mapper.readValue(mvcResult.getResponse().getContentAsString(), type);
    }


}