package com.yapp.sharefood.common.controller.documentation;

import org.springframework.test.web.servlet.ResultHandler;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class DocumentationUtils {
    public static ResultHandler documentIdentify(String identify) {
        return document(identify, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
    }
}