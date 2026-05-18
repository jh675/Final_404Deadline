// src/main/resources/static/js/common/dateUtils.js

const dateUtils = {


	today: function()	{
		format(new Date(),'YYYY-MM-DD')
	},
	gettime: function(date, 'YYYY-MM-DD HH:mm')	{
		format(date,'YYYY-MM-DD')
	},

  // 1. 포맷 변환 (YYYY-MM-DD 또는 커스텀 포맷)
  format: function(date, formatStr = 'YYYY-MM-DD') {
    if (!date) return '-';
    const d = new Date(date);
    if (isNaN(d.getTime())) return '-';

    const pad = (num) => String(num).padStart(2, '0');
    const yyyy = d.getFullYear();
    const mm = pad(d.getMonth() + 1);
    const dd = pad(d.getDate());
    const hh = pad(d.getHours());
    const mi = pad(d.getMinutes());

    if (formatStr === 'YYYY-MM-DD HH:mm') {
      return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
    }
    return `${yyyy}-${mm}-${dd}`;
  },

  // 2. 날짜 더하기/빼기
  add: function(date, amount, unit = 'day') {
    const d = new Date(date);
    if (unit === 'day') d.setDate(d.getDate() + amount);
    if (unit === 'month') d.setMonth(d.getMonth() + amount);
    if (unit === 'year') d.setFullYear(d.getFullYear() + amount);
    return d;
  },

  // 3. 날짜 차이 계산
  getDiff: function(fromDate, toDate, unit = 'day') {
    const fDate = new Date(fromDate);
    const tDate = new Date(toDate);
    const diffTime = tDate - fDate;
    return Math.floor(diffTime / (1000 * 60 * 60 * 24));
  },

  // 4. 월 시작일/종료일 구하기
  getMonthRange: function(date) {
    const d = new Date(date);
    const y = d.getFullYear();
    const m = d.getMonth();
    
    const start = this.format(new Date(y, m, 1));
    const end = this.format(new Date(y, m + 1, 0));
    return { start, end };
  },

  // 5. 유효성 검증
  isValid: function(dateStr) {
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    if (!regex.test(dateStr)) return false;
    
    const parts = dateStr.split("-");
    const y = parseInt(parts[0], 10);
    const m = parseInt(parts[1], 10) - 1;
    const d = parseInt(parts[2], 10);
    
    const date = new Date(y, m, d);
    return date.getFullYear() === y && date.getMonth() === m && date.getDate() === d;
  },

  // 6. 상대적 시간 표시
  toRelative: function(date) {
    const now = new Date();
    const diffMin = Math.floor((now - new Date(date)) / (1000 * 60));
    
    if (diffMin < 1) return '방금 전';
    if (diffMin < 60) return `${diffMin}분 전`;
    
    const diffHour = Math.floor(diffMin / 60);
    if (diffHour < 24) return `${diffHour}시간 전`;
    
    return this.format(date);
  }
};