package ca.bc.gov.educ.api.penrequest.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.bc.gov.educ.api.penrequest.mappers.DocumentMapper;
import ca.bc.gov.educ.api.penrequest.mappers.DocumentMapperImpl;
import ca.bc.gov.educ.api.penrequest.struct.Document;
import ca.bc.gov.educ.api.penrequest.struct.DocumentMetadata;
import ca.bc.gov.educ.api.support.DocumentBuilder;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DocumentMapperImpl.class})
@AutoConfigureJsonTesters
public class DocumentEntityJsonTests {
    @Autowired
    private JacksonTester<Document> jsonTester;

    @Autowired
    private JacksonTester<DocumentMetadata> documentMetadataTester;

    @Autowired
    private final DocumentMapper mapper = DocumentMapper.mapper;

    private DocumentEntity document;

    @Before
    public void setUp() {
        this.document = new DocumentBuilder().build();
    }

    @Test
    public void documentSerializeTest() throws Exception { 
        JsonContent<Document> json = this.jsonTester.write(mapper.toStructure(this.document)); 

        assertThat(json).hasJsonPathStringValue("@.documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
            .isEqualToIgnoringCase("BCSCPHOTO");
        assertThat(json).extractingJsonPathStringValue("@.documentData")
            .isEqualToIgnoringCase("TXkgY2FyZCE=");
        
        assertThat(json).doesNotHaveJsonPathValue("@.penRequest");
    }

    @Test
    public void documentMetadataSerializeTest() throws Exception { 
        JsonContent<DocumentMetadata> json = this.documentMetadataTester.write(mapper.toMetadataStructure(this.document)); 

        assertThat(json).hasJsonPathStringValue("@.documentID");
        assertThat(json).extractingJsonPathStringValue("@.documentTypeCode")
            .isEqualToIgnoringCase("BCSCPHOTO");
        assertThat(json).doesNotHaveJsonPathValue("@.documentData");
        
        assertThat(json).doesNotHaveJsonPathValue("@.penRequest");
    }

    @Test
    public void documentDeserializeTest() throws Exception {
        Document document = this.jsonTester.readObject("document.json");
        DocumentEntity documentEntity = mapper.toModel(document);
        assertThat(documentEntity.getDocumentData()).isEqualTo("My card!".getBytes());
    }

    @Test
    public void documentDeserializeWithExtraTest() throws Exception {
        Document document = this.jsonTester.readObject("document-extra-properties.json");
        assertThat(document.getDocumentData()).isEqualTo("TXkgY2FyZCE=");
    }

}