package com.example.demo;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class HtmlTagInjectFilter implements Filter {

    private static final String HEAD_START_TAG = "<head>";
    private static final String HEAD_END_TAG = "</head>";
    private static final String BODY_START_TAG = "<body>";
    private static final String BODY_END_TAG = "</body>";

    // 你想插入的内容（可改成从配置或数据库读取）
    private static final String INSERT_HEAD_START = "<script>console.log('Head 先頭');</script>";
    private static final String INSERT_HEAD_END = "<script>console.log('Head 末尾');</script>";
    private static final String INSERT_BODY_START = "<script>console.log('Body 先頭');</script>";
    private static final String INSERT_BODY_END = "<script>console.log('Body 末尾');</script>";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.endsWith(".html")) {
            InputStream is = request.getServletContext().getResourceAsStream(path);
            if (is != null) {
                // ファイルのエンコーディングに合わせてください
                String encoding = "UTF-8";
                String html = new String(is.readAllBytes(), encoding);
                is.close();

                if (html.contains(HEAD_START_TAG)) {
                    html = html.replace(HEAD_START_TAG, HEAD_START_TAG + INSERT_HEAD_START);
                }
                if (html.contains(HEAD_END_TAG)) {
                    html = html.replace(HEAD_END_TAG, INSERT_HEAD_END + HEAD_END_TAG);
                }
                if (html.contains(BODY_START_TAG)) {
                    html = html.replace(BODY_START_TAG, BODY_START_TAG + INSERT_BODY_START);
                }
                if (html.contains(BODY_END_TAG)) {
                    html = html.replace(BODY_END_TAG, INSERT_BODY_END + BODY_END_TAG);
                }

                byte[] bytes = html.getBytes(encoding);

                response.setContentType("text/html;charset=" + encoding);
                response.setContentLength(bytes.length);
                response.setCharacterEncoding(encoding);
                response.getOutputStream().write(bytes);
                response.getOutputStream().flush();

                return;
            }
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
