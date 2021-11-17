package com.yapp.sharefood.common.documentation;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.test.web.servlet.ResultHandler;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class DocumentationUtils {

    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                prettyPrint(),
                modifyUris().host("localhost").removePort()
        );
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    public static ResultHandler documentIdentify(String identify) {
        return document(identify, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
    }
}