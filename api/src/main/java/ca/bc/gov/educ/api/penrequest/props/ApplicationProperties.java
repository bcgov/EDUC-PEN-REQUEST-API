package ca.bc.gov.educ.api.penrequest.props;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    public static final String CLIENT_ID = "PEN-REQUEST-API";

	@Value("${file.maxsize}")
	private int maxFileSize;

	@Value("${file.extensions}")
	private List<String> fileExtensions;

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public List<String> getFileExtensions() {
		return fileExtensions;
	}
}
