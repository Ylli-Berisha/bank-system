import { ref } from 'vue';

export function useConfirmModal() {
    const isOpen = ref(false);
    const title = ref('');
    let onConfirmCallback = null;
    let onCancelCallback = null;

    function openModal(newTitle, onConfirm, onCancel) {
        title.value = newTitle;
        onConfirmCallback = onConfirm;
        onCancelCallback = onCancel;
        isOpen.value = true;
    }

    function confirm() {
        isOpen.value = false;
        if (onConfirmCallback) onConfirmCallback();
    }

    function cancel() {
        isOpen.value = false;
        if (onCancelCallback) onCancelCallback();
    }

    return {
        isOpen,
        title,
        openModal,
        confirm,
        cancel,
    };
}
