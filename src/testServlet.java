import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        createXML(request, response,"GET");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        createXML(request, response,"POST");
    }

    private void createXML(HttpServletRequest request, HttpServletResponse response, String type) throws IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.append("<request_detail>\n");
        pw.append("<client_info>\n");
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        pw.append("<ip-address>"+ipAddress+"</ip-address>\n");
        pw.append("<user-agent>"+request.getHeader("User-Agent")+"</user-agent>\n");
        pw.append("</client_info>\n");
        pw.append("<parameters method=\""+type+"\"/>\n");

        boolean numericParameterIsApended = false;
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                if (isNumeric(value)) {
                    if (!numericParameterIsApended){
                        pw.append("<numeric_parameters>\n");
                        numericParameterIsApended=true;
                    }
                   pw.append("<parameter name=\""+key+"\">"+value+"</parameter>\n");
                }
            }
        }
        if (numericParameterIsApended) {
            pw.append("</numeric_parameters>\n");
        }

        boolean stringParameterIsAppended = false;
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                if (!isNumeric(value)) {
                    if (!stringParameterIsAppended){
                        pw.append("<string_parameters>\n");
                        stringParameterIsAppended=true;
                    }
                    pw.append("<parameter name=\""+key+"\">"+value+"</parameter>\n");
                }
            }
        }
        if (stringParameterIsAppended)
            pw.append("</string_parameters>\n");

        pw.append("</request_detail>\n");
    }

    private boolean isNumeric(String value) {
        // TODO подумать
        Pattern p = Pattern.compile("(^[0-9]{1,10}$)|(^[0-9]{1,10}[,|.]{1}[0-9]{1,10}$)");
        Matcher m = p.matcher(value);
        return m.matches();
    }



}
