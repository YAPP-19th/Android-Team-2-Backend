# Api Docs

## How to create

gralde -> build

---

## convention

- path : [ host ]/[ feature ]
- pathExample : localhost:8080/users
- fileName : [ javaMethodName ]-[ HttpMethodType ].html
- fileNameExample : nickname-get.html
- localhost:8080/user/nickname-get.html

## Guide

+ Common
<pre>
<code>
//if use auth feature
class ExampleControllerTest extends PreprocessController { ... }

//if not use auth feature
class ExampleControllerTest extends DocumentTest { ... }
</code>
</pre>

+ Success Case
<pre>
<code>
//given
...

//when
...

//then
Response response = objectMapper.readValue(
                          perform.andExpect(status().isOk())
                                .andDo(documentIdentify([FilePath]) //FilePath : docs snippets path
                                ...
                                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                    });

//assertLogic
assertEquals(..., ...);
</code>
</pre>
+ Fail Case
<pre>
<code>
public class SampleException extends RuntimeException {
  public static final String SAMPLE_EXCEPTION_MSG = "SAMPLE_EXCEPTION_MSG";

  ...
}

//given
willThrow(new SampleException()).given(sampleService).sampleMethod();
...

//when
...

//then
String errMsg = perform.andExpect(status().isNotFound())
                      .andDo(documentIdentify([FILE_PATH]) //FILE_PATH : docs snippets path
                      ...
                      .getContentAsString(StandardCharsets.UTF_8);

//assertLogic
assertThat(errMsg).isNotNull()
                .isNotEmpty()
                .isEqualTo(SAMPLE_EXCEPTION_MSG);
</code>
</pre>

---

## Complete Docs

- auth
- user
    - nickname-get
    - nickname-patch
    - nickname-validation-get
    - me-get
    - other-get
- userFlavor
- bookmark
- category
- tag

---



