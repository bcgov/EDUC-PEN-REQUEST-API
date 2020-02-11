package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.exception.RestExceptionHandler;
import ca.bc.gov.educ.api.penrequest.model.DocumentEntity;
import ca.bc.gov.educ.api.penrequest.model.PenRequestEntity;
import ca.bc.gov.educ.api.penrequest.props.ApplicationProperties;
import ca.bc.gov.educ.api.penrequest.repository.DocumentRepository;
import ca.bc.gov.educ.api.penrequest.repository.DocumentTypeCodeTableRepository;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.support.DocumentBuilder;
import ca.bc.gov.educ.api.support.DocumentTypeCodeBuilder;
import ca.bc.gov.educ.api.support.PenRequestBuilder;
import ca.bc.gov.educ.api.support.WithMockOAuth2Scope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
public class DocumentWebMvcTests {
  //@Autowired
  private MockMvc mvc;

  @Autowired
  DocumentController documentController;

  @Autowired
  private DocumentRepository repository;

  @Autowired
  private PenRequestRepository penRequestRepository;

  @Autowired
  private DocumentTypeCodeTableRepository documentTypeCodeRepository;

  @Autowired
  private ApplicationProperties props;

  private UUID documentID;

  private UUID penReqID = UUID.randomUUID();

  @Before
  public void setUp() {

    DocumentTypeCodeBuilder.setUpDocumentTypeCodes(documentTypeCodeRepository);
    mvc = MockMvcBuilders.standaloneSetup(documentController)
            .setControllerAdvice(new RestExceptionHandler()).build();

    PenRequestEntity penRequest = new PenRequestBuilder()
            .withoutPenRequestID().build();
    DocumentEntity document = new DocumentBuilder()
            .withoutDocumentID()
            //.withoutCreateAndUpdateUser()
            .withPenRequest(penRequest)
            .withTypeCode("CAPASSPORT")
            .build();
    penRequest = this.penRequestRepository.save(penRequest);
    document = this.repository.save(document);
    this.penReqID = penRequest.getPenRequestID();
    this.documentID = document.getDocumentID();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DOCUMENT")
  public void readDocumentTest() throws Exception {
    this.mvc.perform(get("/" + this.penReqID.toString() + "/documents/" + this.documentID.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData", is("TXkgY2FyZCE=")));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
  public void createDocumentTest() throws Exception {
    this.mvc.perform(post("/" + this.penReqID.toString() + "/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", not(is(this.documentID.toString()))))
            .andExpect(jsonPath("$.documentTypeCode", is("BCSCPHOTO")))
            .andExpect(jsonPath("$.documentData").doesNotExist())
            .andExpect(jsonPath("$.penRequestID").doesNotExist());
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
  public void createDocumentWithInvalidFileSizeTest() throws Exception {
    this.mvc.perform(post("/" + this.penReqID.toString() + "/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req-invalid-filesize.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.message", containsStringIgnoringCase("documentData")));
  }

  @Test
  @WithMockOAuth2Scope(scope = "WRITE_DOCUMENT")
  public void createDocumentWithoutDocumentDataTest() throws Exception {
    this.mvc.perform(post("/" + this.penReqID.toString() + "/documents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Files.readAllBytes(new ClassPathResource(
                    "../model/document-req-without-doc-data.json", DocumentWebMvcTests.class).getFile().toPath()))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.subErrors[0].field", is("documentData")));
  }

  @Test
  @WithMockOAuth2Scope(scope = "DELETE_DOCUMENT")
  public void deleteDocumentTest() throws Exception {
    this.mvc.perform(delete("/" + this.penReqID.toString() + "/documents/" + this.documentID.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.documentData").doesNotExist());


    assertThat(repository.findById(this.documentID).isPresent()).isFalse();
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DOCUMENT")
  public void readAllDocumentMetadataTest() throws Exception {
    this.mvc.perform(get("/" + this.penReqID.toString() + "/documents")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.length()", is(1)))
            .andExpect(jsonPath("$.[0].documentID", is(this.documentID.toString())))
            .andExpect(jsonPath("$.[0].documentTypeCode", is("CAPASSPORT")))
            .andExpect(jsonPath("$.[0].documentData").doesNotExist());
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DOCUMENT_REQUIREMENTS")
  public void getDocumentRequirementsTest() throws Exception {
    this.mvc.perform(get("/file-requirements")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.maxSize", is(props.getMaxFileSize())))
            .andExpect(jsonPath("$.extensions.length()", is(props.getFileExtensions().size())))
            .andExpect(jsonPath("$.extensions[0]", is(props.getFileExtensions().get(0))));
  }

  @Test
  @WithMockOAuth2Scope(scope = "READ_DOCUMENT_TYPES")
  public void getDocumentTypesTest() throws Exception {
    this.mvc.perform(get("/document-types")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.length()", is(2)));
  }

}
