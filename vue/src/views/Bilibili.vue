<template>
  <div class="bilibili-container">
    <!-- 顶部导航栏 -->
    <header class="bili-header">
      <div class="header-container">
        <div class="logo">
          <span class="bili-icon">哔哩哔哩</span>
        </div>
        <div class="search-bar">
          <input type="text" v-model="searchQuery" placeholder="每天都有新发现" @keyup.enter="searchVideos">
          <button class="search-btn" @click="searchVideos">搜索</button>
        </div>
        <div class="user-actions">
          <button class="upload-btn" @click="showUploadForm = true">
            <i class="iconfont icon-upload"></i> 投稿
          </button>
        </div>
      </div>

      <!-- 编辑视频信息弹窗 -->
      <div class="modal-overlay" v-if="showEditDialog" @click.self="closeEditDialog">
        <div class="upload-modal">
          <div class="modal-header">
            <h3>编辑视频信息</h3>
            <span class="close-btn" @click="closeEditDialog">&times;</span>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>标题</label>
              <input v-model="editForm.title" type="text" class="text-input" />
            </div>
            <div class="form-group" style="margin-top: 12px;">
              <label>简介</label>
              <textarea v-model="editForm.description" class="text-area" rows="4"></textarea>
            </div>
            <div class="form-actions">
              <button class="cancel-btn" @click="closeEditDialog">取消</button>
              <button class="upload-confirm-btn" @click="saveEdit">保存</button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 上传视频弹窗 -->
    <div class="modal-overlay" v-if="showUploadForm" @click.self="showUploadForm = false">
      <div class="upload-modal">
        <div class="modal-header">
          <h3>上传视频</h3>
          <span class="close-btn" @click="showUploadForm = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="upload-area" 
               @dragover.prevent="dragOver = true" 
               @dragleave="dragOver = false"
               @drop.prevent="handleDrop"
               :class="{'drag-over': dragOver}">
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleFileSelect" 
              accept="video/*" 
              style="display: none;"
            >
            <div v-if="!isUploading" class="upload-content">
              <i class="iconfont icon-upload-cloud"></i>
              <p>将视频拖到此处，或<button class="browse-btn" @click.stop="$refs.fileInput.click()">浏览文件</button></p>
              <p class="file-hint">支持MP4、WebM等视频格式，文件大小不超过2GB</p>
              <div v-if="selectedFile" class="file-preview">
                <div class="file-info">
                  <i class="iconfont icon-video"></i>
                  <div class="file-details">
                    <div class="file-name">{{ selectedFile.name }}</div>
                    <div class="file-size">{{ formatFileSize(selectedFile.size) }}</div>
                  </div>
                  <button class="remove-btn" @click.stop="removeFile">
                    <i class="iconfont icon-close"></i>
                  </button>
                </div>
              </div>
              <button 
                class="upload-confirm-btn" 
                :disabled="!selectedFile"
                @click.stop="uploadVideo">
                开始上传
              </button>
            </div>
            <div v-else class="uploading-content">
              <div class="upload-progress">
                <div class="progress-bar">
                  <div class="progress" :style="{ width: uploadProgress + '%' }"></div>
                </div>
                <div class="progress-text">
                  <span>上传中... {{ uploadProgress }}%</span>
                  <span>{{ uploadSpeed }}/s • {{ timeRemaining }}</span>
                </div>
              </div>
              <button class="cancel-btn" @click.stop="cancelUpload">取消</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 播放时：左侧播放器 + 右侧字幕面板（全屏） -->
    <template v-if="currentVideo">
      <main class="main-content playing-mode">
        <div class="playing-layout">
          <!-- 左侧播放器区域 -->
          <div class="player-section">
            <!-- 视频播放器 -->
            <div class="video-player-container">
              <div class="video-player-wrapper">
                <button class="player-close-btn" @click="closePlayer" v-if="currentVideo">×</button>
                <video 
                  ref="videoPlayer" 
                  controls 
                  autoplay
                  class="video-player"
                  @play="onVideoPlay"
                  @pause="onVideoPause"
                  @timeupdate="onTimeUpdate"
                  @ended="onVideoEnd"
                  @fullscreenchange="onFullscreenChange"
                >
                  <source :src="currentVideo" type="video/mp4">
                  您的浏览器不支持 HTML5 视频标签。
                </video>
                <div class="video-controls" v-if="showVideoControls">
                  <div class="progress-bar">
                    <div class="progress" :style="{ width: playProgress + '%' }"></div>
                    <div class="progress-buffer" :style="{ width: bufferProgress + '%' }"></div>
                  </div>
                  <div class="control-buttons">
                    <button class="control-btn" @click="togglePlay">
                      <i class="iconfont" :class="isPlaying ? 'icon-pause' : 'icon-play'"></i>
                    </button>
                    <div class="time-display">
                      {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
                    </div>
                    <div class="right-controls">
                      <button class="control-btn" @click="toggleMute">
                        <i class="iconfont" :class="isMuted ? 'icon-mute' : 'icon-volume'"></i>
                      </button>
                      <input 
                        type="range" 
                        v-model="volume" 
                        min="0" 
                        max="1" 
                        step="0.1" 
                        class="volume-slider"
                        @input="onVolumeChange"
                      >
                      <button class="control-btn" @click="toggleFullscreen">
                        <i class="iconfont icon-fullscreen"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="video-info">
                <h2 class="video-title">{{ currentVideoTitle }}</h2>
                <div class="video-meta">
                  <span class="view-count"><i class="iconfont icon-play"></i> {{ viewCount }}次播放</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧字幕/文字面板 -->
          <div class="subtitle-panel">
            <div class="subtitle-header">
              <h3>学习文本</h3>
              <div class="subtitle-controls">
                <label>字体大小:</label>
                <input 
                  type="range" 
                  v-model="subtitleFontSize" 
                  min="12" 
                  max="32" 
                  step="2"
                  class="font-size-slider"
                >
                <span class="font-size-display">{{ subtitleFontSize }}px</span>
              </div>
            </div>
            <textarea 
              v-model="subtitleText"
              class="subtitle-textarea"
              :style="{ fontSize: subtitleFontSize + 'px' }"
              placeholder="在这里输入或粘贴文本内容，用于学习英语..."
            ></textarea>
            <div class="subtitle-actions">
              <button class="action-btn" @click="saveSubtitle" :disabled="isSavingSubtitle">
                {{ isSavingSubtitle ? '保存中...' : '保存' }}
              </button>
              <button class="action-btn" @click="copySubtitle">复制</button>
              <button class="action-btn action-btn-danger" @click="clearSubtitle">清空</button>
            </div>
          </div>
        </div>
      </main>
    </template>

    <!-- 未播放时：视频列表 -->
    <template v-else>
      <main class="main-content">
        <!-- 视频列表 -->
        <div class="video-list-container">
          <div class="section-header">
            <h2 class="section-title">{{ sectionTitle }}</h2>
          </div>
          <div class="video-grid">
            <div 
              v-for="(video, index) in filteredVideos" 
              :key="index" 
              class="video-card"
              @click="playVideo(video)">
              <div class="video-thumbnail">
                <img :src="getThumbnailUrl(video.thumbnail || 'default-thumbnail.jpg')" :alt="video.title">
                <div class="video-duration">{{ formatDuration(video.duration) }}</div>
                <div class="play-overlay">
                  <i class="iconfont icon-play"></i>
                </div>
                <div class="video-actions">
                  <button class="more-btn" @click.stop="toggleMenu(index)">···</button>
                  <div class="action-menu" v-if="menuVisibleIndex === index">
                    <button class="menu-item" @click.stop="editVideo(video)">编辑</button>
                    <button class="menu-item delete" @click.stop="deleteVideo(video)">删除</button>
                  </div>
                </div>
              </div>
              <div class="video-info">
                <h3 class="video-title">{{ video.title }}</h3>
                <div class="video-meta">
                  <span class="up-name">{{ video.author }}</span>
                  <span class="view-count">{{ formatViewCount(video.views) }}观看</span>
                  <span class="publish-time">{{ formatTimeAgo(video.publishTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </template>
  </div>
</template>

<script>
export default {
  name: 'Bilibili',
  data() {
    return {
      searchQuery: '',
      showUploadForm: false,
      dragOver: false,
      isUploading: false,
      uploadProgress: 0,
      uploadSpeed: '0 B',
      timeRemaining: '--',
      selectedFile: null,
      uploadRequest: null,
      // 视频列表与播放相关
      videos: [],
      filteredVideos: [],
      currentVideo: '',
      currentVideoTitle: '',
      viewCount: 0,
      showVideoControls: true,
      // 播放控制相关
      isPlaying: false,
      isMuted: false,
      volume: 1,
      playProgress: 0,
      bufferProgress: 0,
      currentTime: 0,
      duration: 0,
      // 列表展示
      sectionTitle: '推荐视频',
      hasMoreVideos: false,
      menuVisibleIndex: -1,
      // 编辑弹窗
      showEditDialog: false,
      editForm: {
        id: null,
        title: '',
        description: ''
      },
      // 字幕/文字面板
      subtitleText: '',
      subtitleFontSize: 16,
      subtitleId: null,
      isSavingSubtitle: false,
      // 其他数据...
    }
  },
  methods: {
    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
      }
    },
    toggleMenu(idx) {
      this.menuVisibleIndex = this.menuVisibleIndex === idx ? -1 : idx;
    },
    editVideo(video) {
      if (!video || !video.id) return;
      this.editForm = {
        id: video.id,
        title: video.title || '',
        description: video.description || ''
      };
      this.showEditDialog = true;
      this.menuVisibleIndex = -1;
    },
    async deleteVideo(video) {
      if (!video || !video.id) return;
      if (!window.confirm('确认删除该视频吗？')) return;

      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      try {
        const resp = await fetch(`${apiBaseUrl}/videos/${video.id}`, {
          method: 'DELETE'
        });
        const data = await resp.json();
        if (data.code === 200) {
          // 如果正在播放当前被删视频，关闭播放器
          if (this.currentVideoTitle === video.title) {
            this.closePlayer();
          }
          // 重新拉取列表
          this.fetchVideos();
        } else {
          alert(data.message || '删除失败');
        }
      } catch (e) {
        console.error('删除视频失败:', e);
        alert('删除视频失败，请稍后重试');
      }
    },
    async saveEdit() {
      if (!this.editForm.id) return;
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      try {
        const resp = await fetch(`${apiBaseUrl}/videos/${this.editForm.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            title: this.editForm.title,
            description: this.editForm.description
          })
        });
        const data = await resp.json();
        if (data.code === 200) {
          this.showEditDialog = false;
          await this.fetchVideos();
        } else {
          alert(data.message || '保存失败');
        }
      } catch (e) {
        console.error('更新视频信息失败:', e);
        alert('更新视频信息失败，请稍后重试');
      }
    },
    closeEditDialog() {
      this.showEditDialog = false;
    },
    handleDrop(event) {
      event.preventDefault();
      this.dragOver = false;
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('video/')) {
        this.selectedFile = file;
      }
    },
    removeFile() {
      this.selectedFile = null;
      this.$refs.fileInput.value = '';
    },
    uploadVideo() {
      if (!this.selectedFile) return;

      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

      this.isUploading = true;
      this.uploadProgress = 0;
      this.uploadSpeed = '0 B';
      this.timeRemaining = '--';

      const formData = new FormData();
      formData.append('file', this.selectedFile);

      const xhr = new XMLHttpRequest();
      this.uploadRequest = xhr;

      const startTime = Date.now();

      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
          const percent = Math.round((e.loaded / e.total) * 100);
          this.uploadProgress = percent;

          const elapsed = (Date.now() - startTime) / 1000; // 秒
          if (elapsed > 0) {
            const speedBytesPerSec = e.loaded / elapsed;
            this.uploadSpeed = this.formatFileSize(speedBytesPerSec) + '/s';
            const remainingBytes = e.total - e.loaded;
            const remainingSec = remainingBytes / speedBytesPerSec;
            this.timeRemaining = remainingSec > 0 ? `${Math.round(remainingSec)}s` : '即将完成';
          }
        }
      });

      xhr.addEventListener('load', async () => {
        this.uploadRequest = null;
        if (xhr.status === 200) {
          try {
            const data = JSON.parse(xhr.responseText);
            if (data.code === 200 && data.data) {
              // 后端返回 JSON 字符串，需要再次解析
              let uploadResult = data.data;
              if (typeof uploadResult === 'string') {
                uploadResult = JSON.parse(uploadResult);
              }
              const fileName = uploadResult.videoFileName;
              const coverUrl = uploadResult.coverUrl;

              // 调用 /api/videos/info 保存视频元数据
              try {
                await fetch(`${apiBaseUrl}/videos/info`, {
                  method: 'POST',
                  headers: {
                    'Content-Type': 'application/json'
                  },
                  body: JSON.stringify({
                    title: this.selectedFile.name,
                    description: '',
                    fileName: fileName,
                    fileUrl: null,
                    coverUrl: coverUrl || null,
                    userId: 1
                  })
                });
              } catch (e) {
                console.error('保存视频信息失败:', e);
              }

              // 刷新首页视频列表
              this.fetchVideos();

              this.isUploading = false;
              this.showUploadForm = false;
              this.selectedFile = null;
              if (this.$refs.fileInput) {
                this.$refs.fileInput.value = '';
              }
              return;
            }
          } catch (e) {
            console.error('解析上传响应失败:', e);
          }
        }
        this.isUploading = false;
        alert('视频上传失败，请稍后重试');
      });

      xhr.addEventListener('error', () => {
        this.uploadRequest = null;
        this.isUploading = false;
        alert('视频上传失败，请检查网络后重试');
      });

      xhr.open('POST', `${apiBaseUrl}/videos/upload`);
      xhr.send(formData);
    },
    cancelUpload() {
      if (this.uploadRequest) {
        try {
          this.uploadRequest.abort();
        } catch (e) {
          console.error('取消上传失败:', e);
        }
        this.uploadRequest = null;
      }
      this.isUploading = false;
      this.uploadProgress = 0;
      this.uploadSpeed = '0 B';
      this.timeRemaining = '--';
      this.selectedFile = null;
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = '';
      }
    },
    searchVideos() {
      const q = this.searchQuery.trim().toLowerCase();
      if (!q) {
        this.filteredVideos = this.videos;
      } else {
        this.filteredVideos = this.videos.filter(v =>
          (v.title || '').toLowerCase().includes(q)
        );
      }
    },
    async fetchVideos() {
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      try {
        const resp = await fetch(`${apiBaseUrl}/videos/list?pageNum=1&pageSize=20`);
        const data = await resp.json();
        if (data.code === 200 && Array.isArray(data.data)) {
          this.videos = data.data.map(v => ({
            id: v.id,
            title: v.title,
            author: 'UP主',
            views: v.viewCount || 0,
            duration: 0,
            publishTime: v.createTime,
            // 通过后端代理获取封面，避免 CORS 问题
            thumbnail: v.coverUrl ? `${apiBaseUrl}/videos/cover/default-cover.jpg` : 'default-thumbnail.jpg',
            fileUrl: v.fileUrl,
            fileName: v.fileName
          }));
          this.filteredVideos = this.videos;
        }
      } catch (e) {
        console.error('获取视频列表失败:', e);
      }
    },
    playVideo(video) {
      if (!video || !video.fileName) return;
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      let fileName = video.fileName || '';
      if (fileName.startsWith('video/')) {
        fileName = fileName.substring('video/'.length);
      }
      this.currentVideo = `${apiBaseUrl}/videos/play/${encodeURIComponent(fileName)}`;
      this.currentVideoTitle = video.title || '';
      this.viewCount = video.views || 0;
      this.isPlaying = true;
      
      // 加载该视频的字幕
      if (video.id) {
        this.loadSubtitle(video.id);
      }
    },
    closePlayer() {
      this.currentVideo = '';
      this.currentVideoTitle = '';
      this.viewCount = 0;
      this.isPlaying = false;
      this.playProgress = 0;
      this.currentTime = 0;
      this.duration = 0;
    },
    onVideoPlay() {
      this.isPlaying = true;
    },
    onVideoPause() {
      this.isPlaying = false;
    },
    onTimeUpdate(event) {
      const video = event.target;
      this.currentTime = video.currentTime || 0;
      this.duration = video.duration || 0;
      if (this.duration > 0) {
        this.playProgress = (this.currentTime / this.duration) * 100;
      }
    },
    onVideoEnd() {
      this.isPlaying = false;
      this.playProgress = 0;
    },
    onFullscreenChange() {},
    togglePlay() {
      const videoEl = this.$refs.videoPlayer;
      if (!videoEl) return;
      if (this.isPlaying) {
        videoEl.pause();
      } else {
        videoEl.play();
      }
    },
    toggleMute() {
      const videoEl = this.$refs.videoPlayer;
      if (!videoEl) return;
      this.isMuted = !this.isMuted;
      videoEl.muted = this.isMuted;
    },
    onVolumeChange() {
      const videoEl = this.$refs.videoPlayer;
      if (!videoEl) return;
      videoEl.volume = this.volume;
    },
    toggleFullscreen() {
      const videoEl = this.$refs.videoPlayer;
      if (!videoEl) return;
      if (document.fullscreenElement) {
        document.exitFullscreen();
      } else if (videoEl.requestFullscreen) {
        videoEl.requestFullscreen();
      }
    },
    getThumbnailUrl(path) {
      return path;
    },
    formatDuration() {
      return '00:00';
    },
    formatViewCount(count) {
      const v = count || 0;
      if (v >= 10000) return (v / 10000).toFixed(1) + '万';
      return String(v);
    },
    formatTimeAgo(timeStr) {
      if (!timeStr) return '';
      return timeStr;
    },
    formatTime(seconds) {
      const s = Number(seconds) || 0;
      const m = Math.floor(s / 60);
      const sec = Math.floor(s % 60);
      const mm = m < 10 ? '0' + m : '' + m;
      const ss = sec < 10 ? '0' + sec : '' + sec;
      return mm + ':' + ss;
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B';
      const k = 1024;
      const sizes = ['B', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    clearSubtitle() {
      if (confirm('确定要清空文本吗？')) {
        this.subtitleText = '';
        this.subtitleFontSize = 16;
      }
    },
    copySubtitle() {
      if (!this.subtitleText) {
        alert('没有文本可复制');
        return;
      }
      navigator.clipboard.writeText(this.subtitleText).then(() => {
        alert('已复制到剪贴板');
      }).catch(() => {
        alert('复制失败，请重试');
      });
    },
    async loadSubtitle(videoId) {
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      try {
        const resp = await fetch(`${apiBaseUrl}/subtitles/video/${videoId}`);
        const data = await resp.json();
        if (data.code === 200 && data.data) {
          this.subtitleText = data.data.content || '';
          this.subtitleFontSize = data.data.fontSize || 16;
          this.subtitleId = data.data.id;
        } else {
          // 没有字幕，清空
          this.subtitleText = '';
          this.subtitleFontSize = 16;
          this.subtitleId = null;
        }
      } catch (e) {
        console.error('加载字幕失败:', e);
      }
    },
    async saveSubtitle() {
      if (!this.currentVideo) {
        alert('请先播放视频');
        return;
      }
      
      // 从当前视频 URL 中提取视频 ID（这里需要从 filteredVideos 中找到对应的视频）
      const currentVideoObj = this.filteredVideos.find(v => {
        const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
        let fileName = v.fileName || '';
        if (fileName.startsWith('video/')) {
          fileName = fileName.substring('video/'.length);
        }
        return this.currentVideo === `${apiBaseUrl}/videos/play/${encodeURIComponent(fileName)}`;
      });
      
      if (!currentVideoObj || !currentVideoObj.id) {
        alert('无法确定视频ID');
        return;
      }
      
      this.isSavingSubtitle = true;
      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
      
      try {
        const payload = {
          videoId: currentVideoObj.id,
          content: this.subtitleText,
          fontSize: this.subtitleFontSize,
          userId: 1 // 默认用户ID
        };
        
        if (this.subtitleId) {
          payload.id = this.subtitleId;
        }
        
        const resp = await fetch(`${apiBaseUrl}/subtitles`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(payload)
        });
        
        const data = await resp.json();
        if (data.code === 200) {
          this.subtitleId = data.data.id;
          alert('字幕已保存');
        } else {
          alert(data.message || '保存失败');
        }
      } catch (e) {
        console.error('保存字幕失败:', e);
        alert('保存失败，请重试');
      } finally {
        this.isSavingSubtitle = false;
      }
    }
  },
  mounted() {
    this.fetchVideos();
  }
}
</script>

<style scoped>
/* 容器样式 */
.bilibili-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f5f5;
}

/* 上传弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.upload-modal {
  background: white;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.close-btn {
  font-size: 24px;
  cursor: pointer;
  color: #999;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 24px;
}

.upload-area {
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  transition: all 0.3s;
  background-color: #fafafa;
}

.upload-area.drag-over {
  border-color: #fb7299;
  background-color: #fff0f5;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-content .iconfont {
  font-size: 48px;
  color: #fb7299;
  margin-bottom: 16px;
}

.browse-btn {
  background: none;
  border: none;
  color: #fb7299;
  cursor: pointer;
  padding: 0 4px;
  font-size: inherit;
}

.file-hint {
  color: #999;
  font-size: 14px;
  margin-top: 8px;
}

.file-preview {
  margin-top: 20px;
  width: 100%;
  max-width: 400px;
}

.file-info {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 12px;
  position: relative;
}

.file-info .iconfont {
  font-size: 24px;
  color: #fb7299;
  margin-right: 12px;
}

.file-details {
  flex: 1;
  text-align: left;
}

.file-name {
  font-size: 14px;
  margin-bottom: 4px;
  word-break: break-all;
}

.file-size {
  font-size: 12px;
  color: #999;
}

.remove-btn {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  margin-left: 8px;
}

.remove-btn:hover {
  color: #fb7299;
}

.upload-confirm-btn {
  margin-top: 20px;
  padding: 8px 24px;
  background-color: #fb7299;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.upload-confirm-btn:disabled {
  background-color: #ffb6c1;
  cursor: not-allowed;
}

.uploading-content {
  width: 100%;
}

.upload-progress {
  margin-bottom: 20px;
}

.progress-bar {
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  margin-bottom: 8px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #fb7299;
  transition: width 0.3s;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.cancel-btn {
  padding: 6px 16px;
  background: none;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  font-size: 14px;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 头部样式 */
.bili-header {
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 100;
  flex-shrink: 0;
}

.header-container {
  max-width: 1600px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.logo {
  margin-right: 40px;
}

.bili-icon {
  color: #fb7299;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-right: 40px;
  flex: 0 1 auto;
  gap: 8px;
}

.search-bar input {
  width: 200px;
  height: 36px;
  border: 1px solid #e7e7e7;
  border-radius: 4px;
  padding: 0 12px;
  outline: none;
  font-size: 14px;
}

.search-btn {
  height: 36px;
  padding: 0 20px;
  background-color: #fb7299;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.user-actions {
  display: flex;
  align-items: center;
}

.upload-btn {
  padding: 6px 16px;
  background-color: #fb7299;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

/* 主要内容区 */
.main-content {
  display: flex;
  flex-direction: row;
  flex: 1;
  width: 100%;
  padding: 16px;
  overflow: hidden;
  gap: 16px;
}

.main-content.playing-mode {
  padding: 0;
  height: 100%;
  max-width: 100% !important;
  margin: 0 !important;
}

/* 侧边导航 */
.side-nav {
  width: 160px;
  margin-right: 0;
  flex-shrink: 0;
  overflow-y: auto;
}

.side-nav .nav-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: #212121;
  text-decoration: none;
  border-radius: 4px;
  margin-bottom: 4px;
  cursor: pointer;
}

.side-nav .nav-item:hover {
  background-color: #f4f4f4;
}

.side-nav .nav-item.active {
  color: #fb7299;
  background-color: #fff1f2;
}

.side-nav .iconfont {
  margin-right: 8px;
  font-size: 20px;
}

/* 播放时的布局 */
.playing-layout {
  display: flex;
  width: 100%;
  height: 100%;
  gap: 16px;
  padding: 16px;
}

.player-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

/* 播放器与列表布局 */
.video-player-container {
  flex: 1;
  margin-right: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  min-width: 0;
  max-height: 100%;
}

.video-list-container {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
}

/* 字幕/文字面板 */
.subtitle-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 0;
}

.subtitle-header {
  padding: 16px;
  border-bottom: 1px solid #e5e5e5;
  background-color: #f9f9f9;
}

.subtitle-header h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #212121;
}

.subtitle-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.subtitle-controls label {
  white-space: nowrap;
}

.font-size-slider {
  width: 120px;
  height: 4px;
  cursor: pointer;
}

.font-size-display {
  min-width: 50px;
  text-align: right;
  color: #1e80ff;
  font-weight: 500;
}

.subtitle-textarea {
  flex: 1;
  padding: 16px;
  border: none;
  outline: none;
  resize: none;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
}

.subtitle-textarea::placeholder {
  color: #999;
}

.subtitle-actions {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #e5e5e5;
  background-color: #f9f9f9;
}

.action-btn {
  flex: 1;
  padding: 8px 16px;
  background-color: #1e80ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.action-btn:hover {
  background-color: #1171ee;
}

.action-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  opacity: 0.6;
}

.action-btn-danger {
  background-color: #ff4d4f;
}

.action-btn-danger:hover {
  background-color: #ff7875;
}

/* 轮播图 */
.banner {
  width: 100%;
  height: 240px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
  background-color: #f4f4f4;
}

.banner-slider {
  width: 100%;
  height: 100%;
  position: relative;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 视频列表 */
.video-list {
  margin-bottom: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #212121;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.video-grid-side {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.video-player-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #000;
}

.player-close-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  cursor: pointer;
  font-size: 18px;
  line-height: 28px;
  text-align: center;
  z-index: 2;
}

.player-close-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

/* 让 video 填满固定比例容器 */
.video-player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #000;
}

.video-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.video-thumbnail {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 封面比例 */
  border-radius: 8px;
  overflow: hidden;
  background-color: #f4f4f4;
  margin-bottom: 8px;
}

.video-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-actions {
  position: absolute;
  top: 6px;
  right: 6px;
}

.more-btn {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 999px;
  background-color: rgba(0, 0, 0, 0.55);
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  line-height: 24px;
  text-align: center;
}

.more-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.action-menu {
  position: absolute;
  top: 30px;
  right: 0;
  min-width: 110px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 8px 16px rgba(0,0,0,0.15);
  padding: 4px 0;
  z-index: 10;
}

.menu-item {
  display: block;
  width: 100%;
  padding: 8px 14px;
  border: none;
  background: none;
  text-align: left;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  color: #333;
}

.menu-item:hover {
  background-color: #f5f5f5;
}

.menu-item.delete {
  color: #e74c3c;
}

.video-card:hover {
  transform: translateY(-4px);
}

.delete-btn {
  position: absolute;
  top: 4px;
  left: 4px;
  padding: 2px 6px;
  font-size: 12px;
  border: none;
  border-radius: 3px;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  cursor: pointer;
}

.delete-btn:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 宽高比 */
  border-radius: 8px;
  overflow: hidden;
  background-color: #f4f4f4;
  margin-bottom: 8px;
}

.video-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}

.video-play {
  position: absolute;
  left: 8px;
  bottom: 8px;
  color: white;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.play-count {
  margin-left: 4px;
}

.video-title {
  font-size: 14px;
  font-weight: 500;
  color: #212121;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  height: 2.8em;
}

.video-meta {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
}

.up-name, .view-count, .danmu-count {
  margin-right: 12px;
}

/* 响应式布局 */
@media (max-width: 1400px) {
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1200px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .side-nav {
    display: none;
  }
  
  .search-bar {
    margin-right: 16px;
  }
  
  .nav-links {
    display: none;
  }
}
</style>
