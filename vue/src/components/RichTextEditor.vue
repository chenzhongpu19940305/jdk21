<template>
  <div class="rich-text-editor">
    <div ref="editorRef" class="editor-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Quill from 'quill'
import 'quill/dist/quill.snow.css'
import { uploadImage, uploadVideo } from '../api/file'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = ref(null)
let quill = null

// 自定义图片处理器
const imageHandler = () => {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'image/*')
  input.click()

  input.onchange = async () => {
    const file = input.files[0]
    if (!file) return

    // 获取当前光标位置
    const range = quill.getSelection(true)
    
    // 插入占位符
    quill.insertText(range.index, '上传中...', 'user')
    quill.setSelection(range.index + 3)

    try {
      const response = await uploadImage(file)
      if (response.code === 200 && response.data) {
        // 删除占位符
        quill.deleteText(range.index, 3)
        // 插入图片
        quill.insertEmbed(range.index, 'image', response.data)
        quill.setSelection(range.index + 1)
      } else {
        quill.deleteText(range.index, 3)
        quill.insertText(range.index, '图片上传失败', 'user')
      }
    } catch (error) {
      quill.deleteText(range.index, 3)
      quill.insertText(range.index, '图片上传失败: ' + (error.message || '未知错误'), 'user')
      console.error('图片上传失败:', error)
    }
  }
}

// 自定义视频处理器
const videoHandler = () => {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'video/*')
  input.click()

  input.onchange = async () => {
    const file = input.files[0]
    if (!file) return

    // 获取当前光标位置
    const range = quill.getSelection(true)
    if (!range) {
      console.error('无法获取光标位置')
      return
    }
    
    // 插入占位符
    quill.insertText(range.index, '上传中...', 'user')
    quill.setSelection(range.index + 3)

    try {
      const response = await uploadVideo(file)
      if (response.code === 200 && response.data) {
        // 删除占位符
        quill.deleteText(range.index, 3)
        
        // 使用HTML方式插入视频
        const videoUrl = response.data
        const videoHtml = `<p><video controls style='max-width: 100%;'><source src='${videoUrl}' type='${file.type}'></video></p>`
        
        // 使用clipboard转换HTML为Delta并插入
        const clipboard = quill.clipboard
        const delta = clipboard.convert(videoHtml)
        const Delta = quill.constructor.import('delta')
        quill.updateContents(
          new Delta().retain(range.index).concat(delta),
          'user'
        )
        quill.setSelection(range.index + delta.length())
      } else {
        quill.deleteText(range.index, 3)
        quill.insertText(range.index, '视频上传失败', 'user')
      }
    } catch (error) {
      quill.deleteText(range.index, 3)
      quill.insertText(range.index, '视频上传失败: ' + (error.message || '未知错误'), 'user')
      console.error('视频上传失败:', error)
    }
  }
}

onMounted(() => {
  if (editorRef.value) {
    quill = new Quill(editorRef.value, {
      theme: 'snow',
      placeholder: props.placeholder,
      modules: {
        toolbar: {
          container: [
            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'color': [] }, { 'background': [] }],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            [{ 'align': [] }],
            ['link', 'image', 'video'],
            ['blockquote', 'code-block'],
            ['clean']
          ],
          handlers: {
            image: imageHandler,
            video: videoHandler
          }
        }
      }
    })

    // 设置初始内容
    if (props.modelValue) {
      quill.root.innerHTML = props.modelValue
    }

    // 监听内容变化
    quill.on('text-change', () => {
      const content = quill.root.innerHTML
      emit('update:modelValue', content)
    })
  }
})

watch(() => props.modelValue, (newVal) => {
  if (quill && quill.root.innerHTML !== newVal) {
    quill.root.innerHTML = newVal || ''
  }
})

onUnmounted(() => {
  if (quill) {
    quill = null
  }
})

// 获取纯文本内容
const getText = () => {
  return quill ? quill.getText() : ''
}

// 获取 HTML 内容
const getHTML = () => {
  return quill ? quill.root.innerHTML : ''
}

// 设置内容
const setContent = (content) => {
  if (quill) {
    quill.root.innerHTML = content || ''
  }
}

// 清空内容
const clear = () => {
  if (quill) {
    quill.setText('')
  }
}

defineExpose({
  getText,
  getHTML,
  setContent,
  clear
})
</script>

<style scoped>
.rich-text-editor {
  width: 100%;
}

.editor-container {
  min-height: 400px;
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.ql-container) {
  font-size: 14px;
  min-height: 400px;
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.ql-editor) {
  min-height: 400px;
  width: 100%;
  flex: 1;
}

:deep(.ql-toolbar) {
  border-top: 1px solid #e5e5e5;
  border-left: 1px solid #e5e5e5;
  border-right: 1px solid #e5e5e5;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  width: 100%;
  box-sizing: border-box;
}

:deep(.ql-container) {
  border-bottom: 1px solid #e5e5e5;
  border-left: 1px solid #e5e5e5;
  border-right: 1px solid #e5e5e5;
  border-top: none;
  border-radius: 0 0 4px 4px;
  width: 100%;
  box-sizing: border-box;
}

:deep(.ql-snow) {
  width: 100%;
  box-sizing: border-box;
}
</style>


