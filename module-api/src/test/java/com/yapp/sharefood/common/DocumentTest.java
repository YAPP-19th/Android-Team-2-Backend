package com.yapp.sharefood.common;

import com.yapp.sharefood.common.documentation.DocumentRequestBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
public class DocumentTest {
    @Autowired
    protected MockMvc mockMvc;

    protected DocumentRequestBuilder documentRequestBuilder;

    void setUp() {
        documentRequestBuilder = new DocumentRequestBuilder();
    }
}
