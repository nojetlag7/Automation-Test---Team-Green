package org.teamgreen.mentorconnectautomation.api;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class RedactingAllureFilter implements Filter {
    private static final Pattern PASSWORD_JSON = Pattern.compile("(\"password\"\\s*:\\s*\")[^\"]*(\")");

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        String request = formatRequest(requestSpec);
        Response response = ctx.next(requestSpec, responseSpec);
        attach(request, response);
        return response;
    }

    private static void attach(String request, Response response) {
        try {
            Allure.addAttachment("HTTP request", "text/plain", request, ".txt");
            Allure.addAttachment("HTTP response", "text/plain", formatResponse(response), ".txt");
        } catch (RuntimeException ignored) {
            // A missing report attachment must not replace the assertion result.
        }
    }

    private static String formatRequest(FilterableRequestSpecification requestSpec) {
        StringBuilder text = new StringBuilder();
        text.append(requestSpec.getMethod()).append(' ').append(requestSpec.getURI()).append('\n');
        requestSpec.getHeaders().forEach(header -> text
                .append(header.getName())
                .append(": ")
                .append(secretHeader(header.getName()) ? "[redacted]" : header.getValue())
                .append('\n'));
        requestSpec.getCookies().asList().forEach(cookie -> text
                .append("Cookie ")
                .append(cookie.getName())
                .append("=[redacted]\n"));
        text.append('\n').append(bodyText(requestSpec.getBody()));
        return text.toString();
    }

    private static String formatResponse(Response response) {
        StringBuilder text = new StringBuilder();
        text.append(response.statusCode()).append('\n');
        response.getHeaders().forEach(header -> text
                .append(header.getName())
                .append(": ")
                .append(secretHeader(header.getName()) ? "[redacted]" : header.getValue())
                .append('\n'));
        String body;
        try {
            body = response.asString();
        } catch (RuntimeException e) {
            body = "";
        }
        text.append('\n').append(body == null ? "" : PASSWORD_JSON.matcher(body).replaceAll("$1[redacted]$2"));
        return text.toString();
    }

    private static boolean secretHeader(String name) {
        String lower = name.toLowerCase();
        return lower.equals("cookie") || lower.equals("set-cookie") || lower.equals("authorization");
    }

    private static String bodyText(Object body) {
        if (body == null) {
            return "";
        }
        if (body instanceof byte[] bytes) {
            return PASSWORD_JSON.matcher(new String(bytes, StandardCharsets.UTF_8)).replaceAll("$1[redacted]$2");
        }
        if (body instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(key, "password".equals(key) ? "[redacted]" : value));
            return copy.toString();
        }
        return PASSWORD_JSON.matcher(String.valueOf(body)).replaceAll("$1[redacted]$2");
    }
}
