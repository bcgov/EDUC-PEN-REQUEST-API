package ca.bc.gov.educ.api.penrequest.controller;

import ca.bc.gov.educ.api.penrequest.endpoint.PenRequestCommentEndpoint;
import ca.bc.gov.educ.api.penrequest.mappers.PenRequestCommentsMapper;
import ca.bc.gov.educ.api.penrequest.service.PenRequestCommentService;
import ca.bc.gov.educ.api.penrequest.struct.PenRequestComments;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@EnableResourceServer
public class PenRequestCommentsController implements PenRequestCommentEndpoint {

  private final PenRequestCommentsMapper mapper = PenRequestCommentsMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final PenRequestCommentService penRequestCommentService;

  PenRequestCommentsController(@Autowired final PenRequestCommentService penRequestCommentService) {
    this.penRequestCommentService = penRequestCommentService;
  }

  @Override
  public List<PenRequestComments> retrieveComments(String penRequestId) {
    val results = new ArrayList<PenRequestComments>();
    getPenRequestCommentService().retrieveComments(UUID.fromString(penRequestId)).forEach(element -> results.add(mapper.toStructure(element)));
    return results;
  }

  @Override
  public PenRequestComments save(String penRequestId, PenRequestComments penRequestComments) {
    return mapper.toStructure(getPenRequestCommentService().save(UUID.fromString(penRequestId), mapper.toModel(penRequestComments)));
  }
}
