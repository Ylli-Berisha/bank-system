import { ref } from 'vue';

export const globalError = ref(null);

const getErrorMessage = (statusCode, defaultMessage) => {
    switch (statusCode) {
        case 400:
            return 'Bad Request: The server could not understand the request.';
        case 401:
            return 'Unauthorized: Please log in again.';
        case 403:
            return 'Forbidden: You do not have permission to perform this action.';
        case 404:
            return 'Not Found: The requested resource was not found.';
        case 409:
            return 'Conflict: This action cannot be completed due to a conflict with the current state.';
        case 500:
            return 'Internal Server Error: Something went wrong on the server.';
        case 503:
            return 'Service Unavailable: The server is temporarily unable to handle the request.';
        default:
            return defaultMessage;
    }
};

export async function apiWrapper(callback, defaultMessage) {
    globalError.value = null;
    try {
        const response = await callback();
        return { success: true, data: response?.data };
    } catch (err) {
        console.error('API call failed:', err);
        const serverMessage = err.response?.data?.message || '';
        const statusCode = err.response?.status;

        globalError.value = serverMessage || getErrorMessage(statusCode, defaultMessage);

        return { success: false, error: globalError.value };
    }
}