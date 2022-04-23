package web.fileupload.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import web.fileupload.message.ResponseMessage;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*"); //需要加這個，才不會因為檔案超過大小後，前端出現CORS的異常，才能正常噴出Response內容
        System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("檔案太大了！"));
    }
}
