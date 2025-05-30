import { defineStore } from 'pinia';

import { uploadFileApi,getFileApi, getImageApi, uploadImgApi} from '../api/api';

export const useFileStore = defineStore('file', () => {

  const uploadFile = async (data) => {
    const response = await uploadFileApi(data);
    return response.data.data;
  };
  const uploadImg = async (data) => {
    const response = await uploadImgApi(data);
    return response.data.data;
  };
  const getFile = async (data) => {
    try {
      const response = await getFileApi(data);
      if (!response || !response.data || response.data.size === 0) {
        throw new Error('文件内容为空');
      }
      return response.data;
    } catch (error) {
      throw error;
    }
  };
  const getImg = async (data) => {
    try {
      const response = await getImageApi(data);
      if (!response || !response.data || response.data.size === 0) {
        throw new Error('文件内容为空');
      }
      const base64 =await  convertFile2Base64(response.data);
      return base64
    } catch (error) {
      throw error;
    }
  };
  return {
    uploadFile,getFile,getImg,uploadImg
  };
});
const convertFile2Base64 = (file) => {
  return new Promise((resolve, reject) => {
    let img = new FileReader()
    img.readAsDataURL(file)
    img.onload = ({ target }) => {
      resolve(target.result)
    }
  })
}