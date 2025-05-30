import { defineStore } from 'pinia';
import { getReviewByUserIdApi, reviewApi } from '../api/api';

export const useReviewStore = defineStore('review', ()=>{
    const review =async (data)=>{
        await reviewApi(data);
    }
    const getReview =async (data)=>{
        const res = await getReviewByUserIdApi(data);
        return res.data.data;
    }
  return {
    review,
    getReview,
  }
});