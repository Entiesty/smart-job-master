<template>
  <div class="signature-pad">
    <canvas ref="canvas" class="signature-canvas"></canvas>
    <div class="signature-actions">
      <el-button type="danger" @click="clearSignature">清除签名</el-button>
      <el-button type="primary" @click="saveSignature">提交签名</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineExpose } from 'vue';
const model = defineModel()
const canvas = ref(null);
let ctx = null;
let isDrawing = false;
let lastX = 0;
let lastY = 0;

// 定义要发射的事件
const emit = defineEmits(['signature-submit']);


// 初始化画布
function initCanvas() {
  if (!canvas.value) return;
  
  const canvasEl = canvas.value;
  ctx = canvasEl.getContext('2d');
  
  // 设置画布大小
  canvasEl.width = canvasEl.offsetWidth;
  canvasEl.height = canvasEl.offsetHeight;
  
  // 设置画布样式
  ctx.strokeStyle = '#000';
  ctx.lineWidth = 2;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';
}

// 添加事件监听
function addEventListeners() {
  if (!canvas.value) return;
  
  const canvasEl = canvas.value;
  
  // 事件监听
  canvasEl.addEventListener('mousedown', startDrawing);
  canvasEl.addEventListener('mousemove', draw);
  canvasEl.addEventListener('mouseup', stopDrawing);
  canvasEl.addEventListener('mouseout', stopDrawing);
  
  // 触摸支持
  canvasEl.addEventListener('touchstart', handleTouchStart);
  canvasEl.addEventListener('touchmove', handleTouchMove);
  canvasEl.addEventListener('touchend', handleTouchEnd);
}

onMounted(() => {
  initCanvas();
  addEventListeners();
});

// 暴露方法给父组件
defineExpose({
  initCanvas
});

function startDrawing(e) {
  isDrawing = true;
  [lastX, lastY] = getPosition(e);
}

function draw(e) {
  if (!isDrawing) return;
  
  const [x, y] = getPosition(e);
  
  ctx.beginPath();
  ctx.moveTo(lastX, lastY);
  ctx.lineTo(x, y);
  ctx.stroke();
  
  lastX = x;
  lastY = y;
}

function stopDrawing() {
  isDrawing = false;
}

function clearSignature() {
  const canvasEl = canvas.value;
  ctx.clearRect(0, 0, canvasEl.width, canvasEl.height);
  // 清除签名时也清除model值
  model.value = "";
}

function saveSignature() {
  const canvasEl = canvas.value;
  const signatureData = canvasEl.toDataURL('image/png');
  model.value = signatureData;
  // 使用emit发射事件，让父组件可以监听
  emit('signature-submit', '');
}

function getPosition(e) {
  const canvasEl = canvas.value;
  const rect = canvasEl.getBoundingClientRect();
  
  if (e.type.includes('touch')) {
    return [
      e.touches[0].clientX - rect.left,
      e.touches[0].clientY - rect.top
    ];
  } else {
    return [
      e.clientX - rect.left,
      e.clientY - rect.top
    ];
  }
}

// 触摸事件处理
function handleTouchStart(e) {
  e.preventDefault();
  const touch = e.touches[0];
  const mouseEvent = new MouseEvent('mousedown', {
    clientX: touch.clientX,
    clientY: touch.clientY
  });
  canvas.value.dispatchEvent(mouseEvent);
}

function handleTouchMove(e) {
  e.preventDefault();
  const touch = e.touches[0];
  const mouseEvent = new MouseEvent('mousemove', {
    clientX: touch.clientX,
    clientY: touch.clientY
  });
  canvas.value.dispatchEvent(mouseEvent);
}

function handleTouchEnd(e) {
  e.preventDefault();
  const mouseEvent = new MouseEvent('mouseup', {});
  canvas.value.dispatchEvent(mouseEvent);
}
</script>

<style scoped>
.signature-pad {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

.signature-canvas {
  width: 100%;
  height: 200px;
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 10px;
}

.signature-actions {
  display: flex;
  justify-content: space-between;
}
</style>