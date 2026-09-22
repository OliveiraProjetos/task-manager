import { ApiError } from "../services/apiClient";

export function isUnauthorizedError(error: unknown): boolean {
    return (
        error instanceof ApiError &&
        error.status === 401
    );
}

export function getUserErrorMessage(error: unknown): string {
    if (!(error instanceof ApiError)) {
        return "An unexpected error occurred.";
    }

    switch (error.status) {
        case 400:
            return "The provided data is invalid.";

        case 403:
            return "You do not have permission to perform this action.";

        case 404:
            return "The requested resource was not found.";

        case 500:
            return "The server encountered an unexpected error.";

        default:
            return "The request could not be completed.";
    }
}