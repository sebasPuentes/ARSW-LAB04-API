package edu.eci.arsw.blueprints.controllers;

public record ApiResponseLAB<T>(int code, String message, T data) {

    public static <T> ApiResponseLAB<T> ok(T data) {
        return new ApiResponseLAB<>(200, "execute ok", data);
    }

    public static <T> ApiResponseLAB<T> created(T data) {
        return new ApiResponseLAB<>(201, "created", data);
    }

    public static <T> ApiResponseLAB<T> accepted(T data) {
        return new ApiResponseLAB<>(202, "accepted", data);
    }

    public static <T> ApiResponseLAB<T> notFound(String message) {
        return new ApiResponseLAB<>(404, message, null);
    }

    public static <T> ApiResponseLAB<T> badRequest(String message) {
        return new ApiResponseLAB<>(400, message, null);
    }

    public static <T> ApiResponseLAB<T> error(int code, String message) {
        return new ApiResponseLAB<>(code, message, null);
    }
}
