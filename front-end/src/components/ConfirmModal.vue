<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content" @animationend="animationEnded" :class="{ popIn: popping }">
      <div class="modal-header">
        <h3>{{ title }}</h3>
      </div>
      <div class="modal-buttons">
        <button @click="confirm" class="confirm-btn" aria-label="Confirm action">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-check"><polyline points="20 6 9 17 4 12"></polyline></svg>
          Confirm
        </button>
        <button @click="cancel" class="cancel-btn" aria-label="Cancel action">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-x"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

defineProps({
  isOpen: Boolean,
  title: String,
  confirm: Function,
  cancel: Function,
});

const popping = ref(false) // Renamed for a slightly less 'bouncy' feel

// Trigger pop-in animation each time modal opens
watch(() => __props.isOpen, (newVal) => {
  if (newVal) {
    popping.value = true
  }
})

function animationEnded() {
  popping.value = false
}
</script>

<style scoped>
/* New animation for a softer pop-in */
@keyframes popIn {
  0% {
    transform: scale(0.9);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7); /* Slightly darker overlay for seriousness */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(3px); /* Slightly stronger blur */
}

.modal-content {
  background: #ffffff; /* Clean white background */
  padding: 2.2rem 2.8rem; /* Slightly more padding */
  border-radius: 10px; /* Slightly less rounded for seriousness */
  max-width: 450px; /* A bit wider */
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2); /* Softer, more subtle shadow */
  position: relative;
  font-family: 'Inter', 'Segoe UI', sans-serif; /* More professional font */
  user-select: none;
  animation: popIn 0.3s ease-out forwards; /* Default entry animation */
}

.modal-content.popIn {
  animation: popIn 0.3s ease-out forwards; /* Keep pop-in consistent */
}

/* No emoji or sparkles for a serious tone */
/* .emoji { ... } */
/* .sparkles { ... } */

.modal-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  margin-bottom: 1.5rem; /* More space */
  background: #f0f4f8; /* Light background for header */
  padding: 0.8rem 1.4rem;
  border-radius: 8px;
  color: #334e68; /* Darker, more professional text color */
  font-weight: 600; /* Slightly less bold */
  font-size: 1.3rem; /* Slightly smaller */
  box-shadow: none; /* No shadow on header */
  border-bottom: 1px solid #e0e6ed; /* Subtle separator */
}

h3 {
  margin: 0; /* Remove default h3 margin */
}

.modal-buttons {
  margin-top: 2rem; /* More space above buttons */
  display: flex;
  justify-content: center;
  gap: 1.5rem; /* More space between buttons */
}

button {
  padding: 0.8rem 2rem; /* More generous padding */
  border-radius: 8px; /* Slightly less rounded buttons */
  border: 1px solid transparent; /* Add a subtle border for consistency */
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); /* Softer button shadow */
  transition: all 0.2s ease-in-out; /* Smoother transition */
  font-family: 'Inter', 'Segoe UI', sans-serif;
  font-size: 1.05rem; /* Slightly larger text */
  user-select: none;
  display: flex;
  align-items: center;
  gap: 0.6rem; /* Space for icon and text */
}

button svg {
  stroke: currentColor; /* Ensure SVG color matches text */
}

.confirm-btn {
  background-color: #3498db; /* Professional blue */
  color: white;
  border-color: #3498db;
}
.confirm-btn:hover {
  background-color: #2980b9; /* Darker blue on hover */
  box-shadow: 0 6px 15px rgba(52, 152, 219, 0.4); /* Blue shadow on hover */
  transform: translateY(-2px); /* Lift effect */
}

.cancel-btn {
  background-color: #95a5a6; /* Neutral gray */
  color: white;
  border-color: #95a5a6;
}
.cancel-btn:hover {
  background-color: #7f8c8d; /* Darker gray on hover */
  box-shadow: 0 6px 15px rgba(149, 165, 166, 0.4); /* Gray shadow on hover */
  transform: translateY(-2px); /* Lift effect */
}
</style>