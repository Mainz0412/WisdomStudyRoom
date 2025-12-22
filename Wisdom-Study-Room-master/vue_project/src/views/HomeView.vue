<template>
  <div class="container">
    <el-container>

        <el-header>
        <h1 class="title">智慧自习室</h1>
        <div style="position:absolute; right:20px; top:10px">
          <el-popover
            placement="bottom"
            title="用户信息"
            width="160"
            :show-arrow="false"
            v-model="showPopover"
            @mouseenter="showPopover = true"
            @mouseleave="showPopover = false"
          >
            <div>
              <h4>昵称: {{userData.userName}}</h4>
              <h4>账号: {{userData.userAccount}}</h4>
              <el-button type="success" @click="modifyUseDialogTab = true" style="font-size: 12px; padding: 8px 15px;">修改昵称</el-button>
              <el-button type="danger" @click="Logout" style="font-size: 12px; padding: 8px 15px;">退出</el-button>
            </div>
            <template #reference>
              <el-avatar style="cursor: pointer;">
                <img src="../assets/portrait.png" alt="用户头像">
              </el-avatar>
            </template>
          </el-popover>
        </div>
      </el-header>

      <el-dialog title="修改用户昵称" v-model="modifyUseDialogTab" width="600px">
        <el-form :model="userData" label-width="80px">
          <el-form-item label="用户名" style="margin-bottom: 15px">
            <el-input v-model="userData.userName" placeholder="请输入新昵称"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="modifyUseDialogTab = false" class="cancel-btn">
              <i class="el-icon-close"></i>取消</el-button>
            <el-button type="primary" @click="modifyNickname" class="save-btn">
              <i class="el-icon-check"></i>修改</el-button>
          </div>
        </template>
      </el-dialog>

      <el-menu default-active="home" class="el-menu-horizontal-demo" @select="handleMenuSelect">
        <el-menu-item index="home" style="font-size: 18px;">首页</el-menu-item>
        <el-menu-item index="booking" style="font-size: 18px;">预约自习室</el-menu-item>
        <el-menu-item index="mybook" style="font-size: 18px;">我的预约</el-menu-item>
        <el-menu-item index="violation" style="font-size: 18px;">违规记录</el-menu-item>
        <el-menu-item index="contact" style="font-size: 18px;">联系我们</el-menu-item>
        <!-- 其他 导航项 -->
      </el-menu>

      <el-main>
        <div v-if="activeMenuItem === 'home'">
          <el-carousel :interval="3000" arrow="always" height="600px">
            <el-carousel-item v-for="room in 3" :key="room">
              <img :src="`./img/room${room}.jpg`" alt="自习室图片" class="homeimage">
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="card-container" v-else-if="activeMenuItem === 'booking'">
          <el-card class="box-card" v-for="room in this.roomData" :key="room.roomId" :body-style="{ padding: '0px' }" style="border-color: black;">
            <img :src="`./img/room${(room.roomId-1)%5+1}.jpg`" alt="自习室图片" class="image">
            <div>
              <span class="bold">自习室{{ room.roomId }}</span>
              <div class="bottom clearfix">
                <el-button type="text" class="button" @click="detailmessage(room.roomId,1)">详细信息</el-button>
              </div>
            </div>
          </el-card>
        </div>

        <div class="card-container" v-else-if="activeMenuItem === 'mybook'">
          <el-card class="box-book-card" v-for="(booking, index) in bookings" :key="index">
            <template #header>
              <div class="header-status">
                <span class="bold">预约信息{{ index + 1 }}</span>
                <el-tag size="small" :type="getStatusType(getBookingStatus(booking))" class="status-tag">
                  {{ getBookingStatus(booking) }}
                </el-tag>
              </div>
            </template>
            <div style="margin-top: -18px">
              <p>日期: {{ booking.reserveTimeBegin.split(' ')[0] }}</p>
              <p>起始时间: {{ booking.reserveTimeBegin.split(' ')[1] }}</p>
              <p>结束时间: {{booking.reserveTimeEnd.split(' ')[1]  }}</p>
              <p>自习室：{{ getRoomName(booking.reserveRoomId) }}</p>
              <p>座位号：{{ booking.reserveSeatLabel ? booking.reserveSeatLabel : formatSeatLabel(booking.reserveRoomId, booking.reserveSeatNumber) }}</p>
            </div>
            <el-button type="text" @click="reservedetail(booking)" style="margin-top: -25px">详细信息</el-button>
          </el-card>
        </div>

        <div class="card-container" v-else-if="activeMenuItem === 'violation'">
          <el-card class="box-book-card" v-for="(vio, index) in violation" :key="index">
            <template #header>
              <div style="margin-top: 10px">
                <span class="bold">违规信息{{ index + 1 }}</span>
              </div>
            </template>
            <div style="margin-top: -18px">
              <p style="margin-bottom: 40px">违规账号: {{vio.userAccount}}</p>
              <p style="margin-bottom: 40px">违规信息: {{vio.logState}}</p>
              <p style="margin-bottom: 40px">违规时间: {{vio.logTime}}</p>
            </div>
          </el-card>
        </div>

        <div v-else-if="activeMenuItem === 'contact'"></div>
      </el-main>
      <el-footer>
        <p>© 2025 智慧自习室</p>
      </el-footer>
    </el-container>

    <el-dialog title="预约详情" v-model="Reservedetail" width="420px" style="font-weight: bold;color: black">
      <div style="margin-top: -25px">
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约ID:{{this.bookingnum.reserveId}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约账号:{{this.bookingnum.reserveUserAccount}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约开始时间:{{this.bookingnum.reserveTimeBegin}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约结束时间:{{this.bookingnum.reserveTimeEnd}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">自习室：{{ getRoomName(this.bookingnum.reserveRoomId) }}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">座位号：{{ this.bookingnum.reserveSeatLabel ? this.bookingnum.reserveSeatLabel : formatSeatLabel(this.bookingnum.reserveRoomId, this.bookingnum.reserveSeatNumber) }}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">签到时间:{{this.bookingnum.timeSignIn}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">签退时间:{{this.bookingnum.timeSignOut}}</p>
         <div  style="display: flex;margin-top: -18px;margin-bottom: 10px">
             <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">当前状态:</p>
             <p :style="{fontSize: '16px', fontWeight: 'normal', textAlign: 'left', color: textColor }">{{ this.reservestate }}</p>
         </div>
        <el-button v-if="!checkedIn" type="primary" @click="checkIn">签到</el-button>
        <el-button v-if="!checkedOut" type="danger" @click="checkOut">签退</el-button>
        <el-button v-if="!bookstate" type="text" @click="openModifyDialog()">修改预约</el-button>
        <el-button type="text" @click="confirmDeleteBooking()">删除预约</el-button>
      </div>
    </el-dialog>

    <el-dialog title="自习室预约" v-model="dialogTableVisible" width="60%" style="font-weight: bold">
      <div class="booking-controls">
        <el-row class="label-row">
          <span class="label">预约日期: </span>
            <el-date-picker
            v-model="form.datetime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期时间"
            format="YYYY-MM-DD"
            :picker-options="pickerOptions"
            @change="checkTime">
          </el-date-picker>
        </el-row>

        <el-row class="label-row">
          <span class="label">预约起始时间: </span>
          <el-time-picker
            v-model="form.startTime"
            value-format="HH:mm"
            placeholder="选择时间"
            :picker-options="timepickerOptions"
            @change="checkTime">
          </el-time-picker>
        </el-row>

        <el-row class="label-row">
          <span class="label">预约结束时间: </span>
          <el-time-picker
            v-model="form.endTime"
            value-format="HH:mm"
            :picker-options="timepickerOptions"
            @change="checkTime"
            placeholder="选择时间">
          </el-time-picker>
        </el-row>
      </div>

      <div class="booking-layout">
        <div class="booking-left">
          <p style="font-weight: bold;color: black">自习室座位图</p>
          <div class="seat-grid">
            <el-button
              v-for="(seat,index) in seatsByRoom[currentIndex]"
              :key="seat.seatId"
              class="seat-btn"
              type="text"
              @click="seatcelect(seat.seatId, seat.seatNumber, index)"
            >
              <div class="seat-caption">{{ seat.seatNumber }}</div>
              <img :src="
              roomseatflag[index] === 0 && roomseatcelect[index] === 0? seatstateImg[index%2]
            : roomseatflag[index] === 1 && roomseatcelect[index] === 0? seatstateImg[index%2+2]
            : roomseatflag[index] === 2 && roomseatcelect[index] === 0? seatstateImg[index%2+4]
            : seatstateImg[index%2+6]"
              class="seat-image" alt="图片加载中">
            </el-button>
          </div>
        </div>
        <div class="booking-right">
          <p style="font-weight: bold;color: black">当天预约情况（绿色为可预约，红色为占用）</p>
          <div v-if="currentIndex && form.datetime" class="schedule-container">
            <div v-for="seat in seatsByRoom[currentIndex]" :key="seat.seatId" class="schedule-row">
              <div class="schedule-seat-label">{{ seat.seatNumber }}</div>
              <div class="schedule-bar"
                   @mousemove="onBarHover($event, seat.seatId)"
                   @mouseleave="onBarLeave">
                <div v-for="r in (seatReservedRanges[seat.seatId] || [])"
                     :key="r.key"
                     class="bar-reserved"
                     :style="{ left: r.left + '%', width: r.width + '%'}"></div>
                <div v-if="barTooltip && barTooltip.seatId === seat.seatId"
                     class="bar-tooltip"
                     :style="{ left: barTooltip.left + '%'}">{{ barTooltip.label }}</div>
              </div>
            </div>
            <div class="schedule-legend">
              <span class="legend-box free"></span><span>可预约</span>
              <span class="legend-box reserved" style="margin-left: 16px"></span><span>已预约</span>
            </div>
          </div>
        </div>
      </div>

          <el-footer class="footer-detail">
            <el-button @click="dialogTableVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitForm">预 约</el-button>
          </el-footer>
    </el-dialog>

    <el-dialog title="自习室座位分布" v-model="ModifyTable" width="60%" style="font-weight: bold">

      <div class="booking-controls">
        <el-row class="label-row">
          <span class="label">预约日期: </span>
            <el-date-picker
            v-model="form.datetime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期时间"
            format="YYYY-MM-DD"
            :picker-options="pickerOptions"
            @change="checkTime">
          </el-date-picker>
        </el-row>

        <el-row class="label-row">
          <span class="label">预约起始时间: </span>
          <el-time-picker
            v-model="form.startTime"
            value-format="HH:mm"
            placeholder="选择时间"
            :picker-options="timepickerOptions"
            @change="checkTime">
          </el-time-picker>
        </el-row>

        <el-row class="label-row">
          <span class="label">预约结束时间: </span>
          <el-time-picker
            v-model="form.endTime"
            value-format="HH:mm"
            :picker-options="timepickerOptions"
            @change="checkTime"
            placeholder="选择时间">
          </el-time-picker>
        </el-row>
      </div>

      <p style="font-weight: bold;color: black">自习室座位图</p>

      <div class="booking-layout">
        <div class="booking-left">
          <div class="seat-grid">
            <el-button
              v-for="(seat,index) in seatsByRoom[currentIndex]"
              :key="seat.seatId"
              class="seat-btn"
              type="text"
              @click="seatcelect(seat.seatId, seat.seatNumber, index)"
            >
              <div class="seat-caption">{{ seat.seatNumber }}</div>
              <img :src="
              roomseatflag[index] === 0 && roomseatcelect[index] === 0? seatstateImg[index%2]
            : roomseatflag[index] === 1 && roomseatcelect[index] === 0? seatstateImg[index%2+2]
            : roomseatflag[index] === 2 && roomseatcelect[index] === 0? seatstateImg[index%2+4]
            : seatstateImg[index%2+6]"
                     class="seat-image" alt="图片加载中">
            </el-button>
          </div>
        </div>
        <div class="booking-right">
          <div v-if="currentIndex && form.datetime" class="schedule-container">
            <div v-for="seat in seatsByRoom[currentIndex]" :key="seat.seatId" class="schedule-row">
              <div class="schedule-seat-label">{{ seat.seatNumber }}</div>
              <div class="schedule-bar"
                   @mousemove="onBarHover($event, seat.seatId)"
                   @mouseleave="onBarLeave">
                <div v-for="r in (seatReservedRanges[seat.seatId] || [])"
                     :key="r.key"
                     class="bar-reserved"
                     :style="{ left: r.left + '%', width: r.width + '%'}"></div>
                <div v-if="barTooltip && barTooltip.seatId === seat.seatId"
                     class="bar-tooltip"
                     :style="{ left: barTooltip.left + '%'}">{{ barTooltip.label }}</div>
              </div>
            </div>
            <div class="schedule-legend">
              <span class="legend-box free"></span><span>可预约</span>
              <span class="legend-box reserved" style="margin-left: 16px"></span><span>已预约</span>
            </div>
          </div>
        </div>
      </div>

      <el-footer class="footer-detail">
        <el-button @click="ModifyTable = false">取 消</el-button>
        <el-button type="primary" @click="modifyForm">修 改</el-button>
      </el-footer>
    </el-dialog >

    <el-dialog title="选择要预约的自习室" v-model="Modifyselectroom" style="width: 600px;margin-left: 31%;margin-top:10%">
      <el-form>
        <el-select v-model="ModifyroomId" placeholder="请选择自习室" style="margin-bottom: 30px">
          <el-option
            v-for="room in roomData"
            :key="room.roomId"
            :label="room.roomName"
            :value="room.roomId"
          ></el-option>
        </el-select>
        <el-button @click="Modifyselectroom = false">取消</el-button>
        <el-button type="primary" @click="confirmSelectRoom">确定</el-button>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      // 用于控制popover的显示
      showPopover: false,
      currentIndex: null,
      ModifyroomId :null,
      ModifyreserveId:null,
      bookingnum:{
        reserveId: '',
        reserveUserAccount: '',
        reserveTimeBegin: '',
        reserveTimeEnd: '',
        reserveRoomId: '',
        reserveSeatNumber: '',
        timeSignIn: '',
        timeSignOut: '',
      },
      userData: {
        userAccount:'',
        userName:'',
        userId:''
      },
      bookings:[],
      violation:[],
      roomseatflag:[],
      roomseatreservenum:[],
      roomseatregreennum:[],
      roomseatinterval:[],
      roomseatcelect:[],
      roomseat:[],
      seatData:[],
      seatnum:[],
      roomData:[],
      reservestate:'',
      seatsByRoom:[],
      seatstateImg:[
        './img/green-right.png',
        './img/green-left.png',
        './img/yellow-right.png',
        './img/yellow-left.png',
        './img/red-right.png',
        './img/red-left.png',
        './img/green-select-right.png',
        './img/green-select-left.png'
      ],
      // 每个座位当天的时间段（08:00-22:30，30分钟粒度）
      seatSchedules: {},
      // 连续条形图的已预约区间（按百分比定位）
      seatReservedRanges: {},
      // 连续条形图的可预约区间（按百分比定位，并包含区间时间）
      seatFreeRanges: {},
      // 悬浮提示数据
      barTooltip: null,
      activeMenuItem: 'home',
      dialogTableVisible: false,
      ModifyTable:false,
      Modifyselectroom:false,
      modifyUseDialogTab:false,
      Reservedetail:false,
      checkedIn: false,
      checkedOut: false,
      bookstate: false,
      form: {
        datetime:'',
        startTime:'',
        endTime: '',
      },
      formLabelWidth: '50px',
      pickerOptions: {
        disabledDate(time) {
          let tomorrow = new Date();
          tomorrow.setDate(tomorrow.getDate() + 2); // 获取明天的日期
          // 如果时间在明天之后或今天之前，则禁用
          return time.getTime() > tomorrow.getTime() || time.getTime() < Date.now()-8.64e7;
        },
      },
      rules:{

      },
    }
  },
  mounted() {
    document.title = this.$route.meta.title;
  },
  computed: {
    "textColor"() {
      if (this.reservestate === '未开始'||this.reservestate === '未签到') {
        return 'red'
      } else if (this.reservestate === '已签到') {
        return 'green'
      }
      else{
        return 'black'
      }
    },
    "timepickerOptions"() {
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      // 解析 this.form.datetime，支持字符串或 Date 对象
      let datetime = new Date(this.form.datetime);
      if (isNaN(datetime.getTime())) {
        const s = String(this.form.datetime || '')
        const parts = s.split('-'); // 期望 YYYY-MM-DD
        if (parts.length >= 3) {
          const y = parseInt(parts[0], 10) || now.getFullYear();
          const m = (parseInt(parts[1], 10) - 1) || now.getMonth();
          const d = parseInt(parts[2], 10) || now.getDate();
          datetime = new Date(y, m, d);
        } else {
          datetime = new Date();
        }
      }
      const datetimeDate = new Date(datetime.getFullYear(), datetime.getMonth(), datetime.getDate());
      // 确定时间范围
      let range;
      if (today < datetimeDate) {
        range = `08:00:00 - 22:59:00`
      } else {
        const hours = now.getHours()
        const minutes = now.getMinutes()
        const currentHour = hours < 10 ? '0' + hours : '' + hours
        const currentMinute = minutes < 10 ? '0' + minutes : '' + minutes
        range = `${currentHour}:${currentMinute}:00 - 22:59:00`
      }
      return {
        "selectableRange": range,
        "format": 'HH:mm', // 确保时间格式与时间范围匹配
      }
    },
  },
  created() {
      this.loadBookings()
      this.loadRooms()
      this.loadSeats()
      this.getUserData()
      this.loadViolation()
  },
  methods: {
    getBookingStatus(booking) {
      if (!booking) return '未知';
      const now = new Date();
      const begin = new Date(booking.reserveTimeBegin);
      const end = new Date(booking.reserveTimeEnd);

      if (booking.timeSignOut) {
        return '已签退';
      }
      if (booking.timeSignIn) {
        if (now > end) return '已自动签退';
        return '已签到';
      }
      if (now < begin) return '未开始';
      if (now > end) return '已过期';
      const diffMin = Math.floor((now - begin) / 60000);
      if (diffMin >= 30) return '迟到30分钟，预约取消';
      return '未签到';
    },
    getStatusType(status) {
      switch (status) {
        case '已签到':
        case '已签退':
          return 'success';
        case '已自动签退':
        case '未开始':
          return 'info';
        case '未签到':
          return 'warning';
        case '已过期':
        case '迟到30分钟，预约取消':
          return 'danger';
        default:
          return 'info';
      }
    },
    formatDateTime(dt) {
      if (!dt || !(dt instanceof Date)) return '';
      const y = dt.getFullYear();
      const m = String(dt.getMonth() + 1).padStart(2, '0');
      const d = String(dt.getDate()).padStart(2, '0');
      const hh = String(dt.getHours()).padStart(2, '0');
      const mm = String(dt.getMinutes()).padStart(2, '0');
      const ss = String(dt.getSeconds()).padStart(2, '0');
      return `${y}-${m}-${d} ${hh}:${mm}:${ss}`;
    },
    modifyNickname(){
      axios.put('user/update', {
        userId : this.userData.userId,
        userName : this.userData.userName
      })
        .then(response => {
          if(response.data.code === 200)
          {
            this.$message.success('修改昵称成功')
            this.modifyUseDialogTab = false
          }
        })
        .catch(error => {
          this.$message.error('修改昵称失败')
        });
    },
    getUserData(){
      const getUserAccount = sessionStorage.getItem('userAccount')
      const url = `user/list?userAccount=${getUserAccount}`
      axios.get(url).then(response => {
        this.userData.userName = response.data[0].userName
        this.userData.userAccount = response.data[0].userAccount
        this.userData.userId = response.data[0].userId
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试')
      })
    },
    Logout(){
      sessionStorage.removeItem('userAccount')
      this.$router.push('/LoginView')
    },
    handleMenuSelect(index) {//界面标题栏
      this.activeMenuItem = index;
      if (index === 'home') {
      }
      if (index === 'booking') {
        this.loadBookings();
      }
      if (index === 'mybook') {
        this.loadBookings();
      }
      if(index === 'violation')
      {
        this.loadViolation()
      }
    },
    loadViolation(){
      const getUserAccount = sessionStorage.getItem('userAccount')
     const url = `log/select?userAccount=${getUserAccount}`
      axios.get(url).then(response => {
        // 处理获取到的房间数据
        this.violation = response.data.map(vio => ({
          logId: vio.logId,
          userAccount:vio.userAccount,
          logState: vio.logState,
          logTime:vio.logTime
        }))
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      })
    },
    todayDate() {
      const today = new Date();
      const year = today.getFullYear();
      const month = (today.getMonth() + 1).toString().padStart(2, '0'); // 月份从0开始，所以加1
      const day = today.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    zeroPaddingTime(hours, minutes) {
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:00`;
    },
    reservedetail(booking){//我的预约界面“详细信息”按钮
      this.bookingnum = booking
      let currentTime = new Date()
      currentTime.setSeconds(0)
      currentTime.setMilliseconds(0)
      let reservetimebegin = new Date(booking.reserveTimeBegin)
      let reservetimeend = new Date(booking.reserveTimeEnd)
      if(booking.timeSignIn != null && booking.timeSignOut === null)
      {
        this.checkedIn = true
        this.checkedOut = false
        this.bookstate = true
        this.reservestate = '已签到'
        if(currentTime > reservetimeend)
        {
          this.checkedOut = true
          this.bookstate = true
          this.reservestate = '已自动签退'
        }
      }
      else if(booking.timeSignOut != null)
      {
        this.checkedIn = true
        this.checkedOut = true
        this.bookstate = true
        this.reservestate = '已签退'
        if(currentTime > reservetimeend)
        {
          this.reservestate = '已过期'
        }
      }
      else{
        this.checkedIn = false
        this.checkedOut = true
        if(currentTime < reservetimebegin)
        { this.bookstate = false
          this.reservestate = '未开始'
        }
        else if(currentTime >= reservetimebegin && currentTime < reservetimeend)
        {
          let interval = (currentTime - reservetimebegin)/1000/3600
          if(interval >= 30 )
          {
            this.checkedIn = true
            this.checkedOut = true
            this.bookstate = true
            this.reservestate = '迟到30分钟，预约取消'
          }
          else{
            this.bookstate = true
            this.reservestate = '未签到'
          }
        }
        else{
          this.checkedIn = true
          this.checkedOut = true
          this.bookstate = true
          this.reservestate = '已过期'
        }
      }
      this.Reservedetail = true
    },
    checkIn(){
      if(this.reservestate === '未签到')
      {
        let currentTime = new Date()
        currentTime.setSeconds(0)
        currentTime.setMilliseconds(0)
        axios.post('sign/in', {
          reserveId : this.bookingnum.reserveId,
          timeSignIn: this.formatDateTime(currentTime)
        }).then(response => {
          if (response.data.code === 200) {
            this.$message.success('签到成功！');
            this.reservestate = '已签到'
            this.checkedIn = true
            this.checkedOut = false
            this.bookingnum.timeSignIn = this.formatDateTime(currentTime);
            // 重新加载预约信息
            this.loadBookings()
          }
        }).catch(error => {
          console.error('请求错误:', error);
          this.$message.error('网络错误，请稍后重试');
        });
      }
      else if(this.reservestate === '未开始')
      {
        this.$message.error('还未到签到时间')
      }
      else if(this.reservestate === '已过期')
      {
        this.$message.error('预约已过期')
      }
    },
    checkOut(){
      let currentTime = new Date()
      currentTime.setSeconds(0)
      currentTime.setMilliseconds(0)
      axios.post('sign/out', {
        reserveId : this.bookingnum.reserveId,
        timeSignOut: this.formatDateTime(currentTime)
      }).then(response => {
        if (response.data.code === 200) {
          this.$message.success('签退成功！');
          this.checkedOut = true
          this.reservestate = '已签退'
          this.bookingnum.timeSignOut = this.formatDateTime(currentTime);
          this.loadBookings()
        }
      }).catch(error => {
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      });
    },
    loadSeats() {
      this.seatnum = []
      const url = 'seat/allseat';
      axios.get(url).then(response => {
          this.seatData = response.data.map(seat =>({
            seatId: seat.seatId,
            seatNumber: seat.seatNumber,
            roomId: seat.roomId
          }))
        // 按 roomId 对座位进行分组
        this.seatsByRoom = this.seatData.reduce((acc, cur) => {
          if (!acc[cur.roomId]) {
            acc[cur.roomId] = [];
          }
          acc[cur.roomId].push(cur);
          return acc;
        }, {});
        // 计算每个房间的座位总数
        Object.keys(this.seatsByRoom).forEach(roomId => {
          this.seatnum.push(this.seatsByRoom[roomId].length);
        });
        }).catch(error => {
          // 处理请求错误
          console.error('请求错误:', error);
          this.$message.error('网络错误，请稍后重试');
        });
    },
    loadRooms() {
      const url = 'room/all';
      axios.get(url).then(response => {
        // 处理获取到的房间数据
        this.roomData = response.data.map(room => ({
          roomId: room.roomId,
          roomName: room.roomName
        }))
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      })
    },

    // 返回房间名称（若不存在则返回id）
    getRoomName(roomId) {
      const room = this.roomData.find(r => r.roomId === roomId);
      return room && room.roomName ? room.roomName : roomId;
    },

    // 格式化座位标签为 "房间名-座位号" 的形式，作为回退
    formatSeatLabel(roomId, seatNumber) {
      const roomName = this.getRoomName(roomId);
      if (seatNumber == null || seatNumber === '') return `${roomName}-`;
      return `${roomName}-${seatNumber}`;
    },
    loadBookings() {
      const getUserAccount = sessionStorage.getItem('userAccount')
      const url = `reserve/list?userAccount=${getUserAccount}`;
      axios.get(url).then(response => {
        this.bookings = response.data
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试')
      });
    },
    submitForm() {
      let allZero = false;
      for (let i = 0; i < this.roomseatcelect.length; i++) {
        if (this.roomseatcelect[i] !== 0) {
          allZero = true;
          break;
        }
      }
      if(this.form.datetime === null || this.form.startTime === null ||this.form.endTime === null)
      {
        this.$message.error('请选择预约时间')
      }
      else if(allZero === false)
      {
        this.$message.error('请选择座位')
      }
      else {
        const getUserAccount = sessionStorage.getItem('userAccount')
        // 规范化时间，避免出现类似 "11:16:00:00" 的错误格式
        const normalizeTime = (dateStr, timeStr) => {
          if (!timeStr) return null;
          const t = String(timeStr).replace(/:00$/, '');
          return `${dateStr} ${t}:00`;
        };

        const payload = {
          reserveUserAccount: getUserAccount,
          // 发送 seatId（整数）给后端，避免类型绑定失败
          reserveSeatNumber: this.seatselectnum,
          reserveRoomId: this.currentIndex,
          reserveTimeBegin: normalizeTime(this.form.datetime, this.form.startTime),
          reserveTimeEnd: normalizeTime(this.form.datetime, this.form.endTime)
        };

        axios.post('reserve/add', payload).then(response => {
          if (response.data && response.data.code === 200) {
            this.$message.success('预约成功！');
            this.dialogTableVisible = false;
            // 刷新当前用户的预约列表和座位状态
            this.loadBookings();
            if (this.currentIndex) this.detailmessage(this.currentIndex, 1);
          } else if (response.data && response.data.msg) {
            this.$message.error(response.data.msg);
          } else {
            this.$message.error('预约失败，请稍后重试');
          }
        }).catch(error => {
          console.error('请求错误:', error);
          // 如果后端返回 400，显示后端返回的消息（如果存在）
          if (error && error.response && error.response.data && error.response.data.msg) {
            this.$message.error(error.response.data.msg);
          } else {
            this.$message.error('网络错误，请稍后重试');
          }
        });
      }
    },
    modifyForm(){
      let allZero = false;
      for (let i = 0; i < this.roomseatcelect.length; i++) {
        if (this.roomseatcelect[i] !== 0) {
          allZero = true;
          break;
        }
      }
      if(this.form.datetime === null || this.form.startTime === null ||this.form.endTime === null)
      {
        this.$message.error('请选择预约时间')
      }
      else if(allZero === false)
      {
        this.$message.error('请选择座位')
      }
      else{
        const getUserAccount = sessionStorage.getItem('userAccount')
        const normalizeTime = (dateStr, timeStr) => {
          if (!timeStr) return null;
          const t = String(timeStr).replace(/:00$/, '');
          return `${dateStr} ${t}:00`;
        };
        const requestBody = {
          reserveId: this.ModifyreserveId,
          reserveUserName: getUserAccount,
          reserveSeatNumber: this.seatselectnum,
          reserveRoomId: this.ModifyroomId,
          reserveTimeBegin: normalizeTime(this.form.datetime, this.form.startTime),
          reserveTimeEnd: normalizeTime(this.form.datetime, this.form.endTime)
        };
        axios.put('reserve/update', requestBody)
          .then(response => {
            if(response.data.code === 200)
            {
              this.$message.success('修改预约成功')
              this.ModifyTable = false
              this.Reservedetail = false
              this.ModifyroomId = null
              this.loadBookings()
            }
          }).catch(error => {
            this.$message.error('修改预约失败')
          });
      }
    },
    openModifyDialog() {
      this.Modifyselectroom = true
      this.ModifyreserveId = this.bookingnum.reserveId
    },
    confirmSelectRoom(){
      if(this.ModifyroomId != null)
      {
        this.detailmessage(this.ModifyroomId,2)
        this.Modifyselectroom = false
      }
      else
      {
        this.$message.error('请选择一个自习室')
      }

    },

    confirmDeleteBooking() {
      // 获取要删除的预约的 reserveId
      const reserveIdToDelete = this.bookingnum.reserveId;
      this.$confirm('确定要删除该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios({
          method: 'delete',
          url: 'reserve/delete',
          data: {
            reserveId : reserveIdToDelete
          }  // 请求参数放在请求体
        }).then(res => {
            if(res.data.code === 200)
            {
              this.loadBookings()
              this.Reservedetail = false
              this.$message.success('删除成功！')
            }
            else{
              this.$message.error('删除失败')
            }

        })
      }).catch(() => {
      });
    },

    detailmessage(index,num){
      const url = `reserve/all`
      this.currentIndex = index//保存自习室房间号
      this.seatselectnum = null
      this.form.startTime = this.zeroPaddingTime((new Date()).getHours(), (new Date()).getMinutes())
      this.form.endTime = null
      this.form.datetime = this.todayDate()
      //初始化座位展示界面
      this.roomseatflag = Array(this.seatnum[this.currentIndex-1]).fill(0)
      this.roomseatreservenum = Array(this.seatnum[this.currentIndex-1]).fill(0)
      this.roomseatregreennum = Array(this.seatnum[this.currentIndex-1]).fill(0)
      this.roomseatinterval = Array(this.seatnum[this.currentIndex-1]).fill(0)
      this.roomseatcelect = Array(this.seatnum[this.currentIndex-1]).fill(0)
      this.seatSchedules = {}

      let currentTime = new Date()
      currentTime.setSeconds(0)
      currentTime.setMilliseconds(0)
      let currentDateEnd = new Date()
      currentDateEnd.setHours(22)
      currentDateEnd.setMinutes(59)
      currentDateEnd.setSeconds(0)
      currentTime.setMilliseconds(0)
      const timeinterval = (currentDateEnd-currentTime)/(1000*60*60)
      axios.get(url).then(response => {
        this.roomseat = response.data;
        if(num === 1)
        {
          this.dialogTableVisible = true
        }
        if(num === 2)
        {
          this.ModifyTable = true
        }

        for(let i = 0; i < this.roomseat.length; i++)
        {
          if (this.roomseat[i].reserveRoomId === index  && this.roomseat[i].reserveState === 0)
          {

            this.roomseatreservenum[this.roomseat[i].reserveSeatNumber-1]++
            //index号房间的各个椅子的预约数据条数
          }
        }
        for (let i = 0; i < this.roomseat.length; i++)
        { const start = new Date(this.roomseat[i].reserveTimeBegin)
          const end = new Date(this.roomseat[i].reserveTimeEnd)
          if (this.roomseat[i].reserveRoomId === index && this.roomseat[i].reserveState === 0)
          {
            if(start >= currentTime)
            {
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (end - start)/(1000*60*60)
            }
            else if(end < currentTime)
            {
              this.roomseatregreennum[this.roomseat[i].reserveSeatNumber-1]++
            }
            else
            {
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (end - currentTime)/(1000*60*60)
            }
          }
        }
        for (let i = 0; i < this.roomseat.length; i++) {
          if(this.roomseatreservenum[i] === this.roomseatregreennum[i]) {
            this.roomseatflag[i] = 0
          }
          if(this.roomseatinterval[i] === timeinterval)
          {
            this.roomseatflag[i] = 2
          }
        }

        // 构建当天每个座位的时间段（可预约/已预约）
        this.buildSeatSchedulesForDate()
      })
    },
    checkTime() {
      if(this.form.endTime !== null||this.form.startTime!==null)
      {
        if (this.form.endTime <= this.form.startTime) {
          this.form.startTime = null
          this.$message.error('结束时间不能小于等于起始时间')
        }
        if (this.form.startTime >= this.form.endTime)
        {
          this.form.endTime = null
          this.$message.error('开始时间不能大于等于结束时间')
        }
      }
      if(this.form.datetime != null && this.form.endTime != null  && this.form.startTime != null){
        let ReserveStartTime = new Date(this.form.datetime +' '+ this.form.startTime)
        let ReserveEndTime = new Date(this.form.datetime +' '+ this.form.endTime)
        let timeinterval = (ReserveEndTime - ReserveStartTime)/(1000*60*60)

        this.roomseatflag = Array(this.seatnum[this.currentIndex-1]).fill(0)
        this.roomseatreservenum = Array(this.seatnum[this.currentIndex-1]).fill(0)
        this.roomseatregreennum = Array(this.seatnum[this.currentIndex-1]).fill(0)
        this.roomseatinterval = Array(this.seatnum[this.currentIndex-1]).fill(0)
        this.roomseatcelect = Array(this.seatnum[this.currentIndex-1]).fill(0)

        for(let i = 0; i < this.roomseat.length; i++)
        {
          if (this.roomseat[i].reserveRoomId === this.currentIndex && this.roomseat[i].reserveState === 0)
          {
            this.roomseatreservenum[this.roomseat[i].reserveSeatNumber-1]++
            //index号房间的各个椅子的预约数据条数
          }
        }

        for (let i = 0; i < this.roomseat.length; i++)
        {
          if (this.roomseat[i].reserveRoomId === this.currentIndex && this.roomseat[i].reserveState === 0)
          {
            const start = new Date(this.roomseat[i].reserveTimeBegin)
            const end = new Date(this.roomseat[i].reserveTimeEnd)
            if(start >= ReserveStartTime && end <= ReserveEndTime)
            {
              /*const timeDifferenceMinutes = (start - currentTimeStamp)/(1000*60)
              if(timeDifferenceMinutes > 30)
              {
                this.$message.success("大于30")
              }*/
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (end - start)/(1000*60*60)
            }
            else if(end < ReserveStartTime || start > ReserveEndTime)
            {
              this.roomseatregreennum[this.roomseat[i].reserveSeatNumber-1]++
            }
            else if(start <= ReserveStartTime && end > ReserveStartTime && end <= ReserveEndTime)
            {
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (end - ReserveStartTime)/(1000*60*60)
            }
            else if(start > ReserveStartTime && start < ReserveEndTime && end >= ReserveEndTime)
            {
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (ReserveEndTime - start)/(1000*60*60)
            }
            else if(start <= ReserveStartTime && end >=ReserveEndTime)
            {
              this.roomseatflag[this.roomseat[i].reserveSeatNumber-1] = 1
              this.roomseatinterval[this.roomseat[i].reserveSeatNumber-1] += (ReserveEndTime - ReserveStartTime)/(1000*60*60)
            }
          }
        }
        for (let i = 0; i < this.roomseat.length; i++) {
          if(this.roomseatreservenum[i] === this.roomseatregreennum[i]) {
            this.roomseatflag[i] = 0
          }
          if(this.roomseatinterval[i] === timeinterval)
          {
            this.roomseatflag[i] = 2
          }
        }
        // 同步更新当天的座位时间段可视化
        this.buildSeatSchedulesForDate()
      }
    },
    // 构建当天（08:00-22:30）每个座位的30分钟时间段标色
    buildSeatSchedulesForDate() {
      if (!this.currentIndex || !this.form.datetime) return;
      const roomId = this.currentIndex;
      // 生成当天的30分钟时间点列表
      const dayStart = new Date(`${this.form.datetime} 08:00:00`);
      const dayEnd = new Date(`${this.form.datetime} 22:30:00`); // 最后一个槽起点
      const slots = [];
      for (let t = new Date(dayStart); t <= dayEnd; t = new Date(t.getTime() + 30 * 60 * 1000)) {
        const hh = String(t.getHours()).padStart(2, '0');
        const mm = String(t.getMinutes()).padStart(2, '0');
        slots.push({ start: new Date(t), end: new Date(t.getTime() + 30 * 60 * 1000), label: `${hh}:${mm}-${hh}:${mm === '00' ? '30' : '00'}` });
      }

      const seats = this.seatsByRoom[roomId] || [];
      const schedules = {};
      const reservedRanges = {};
      const freeRanges = {};
      // 为每个座位生成槽位标记
      seats.forEach(seat => {
        const seatId = seat.seatId;
        const seatSlots = slots.map(s => ({ ...s, status: 'free' }));
        // 找出该座位当天的预约记录
        const reservations = (this.roomseat || []).filter(r => r.reserveRoomId === roomId && r.reserveState === 0);
        const seatReservations = reservations.filter(r => {
          // 兼容不同 seat 标识：优先 seatId
          const rSeatId = r.reserveSeatNumber; // 后端约定为 seat_id
          return rSeatId === seatId;
        });
        // 将与槽位重叠的标记为 reserved
        seatSlots.forEach(slot => {
          for (const r of seatReservations) {
            const rStart = new Date(r.reserveTimeBegin);
            const rEnd = new Date(r.reserveTimeEnd);
            // 判断重叠：slot.start < rEnd && slot.end > rStart
            if (slot.start < rEnd && slot.end > rStart) {
              slot.status = 'reserved';
              break;
            }
          }
        });
        schedules[seatId] = seatSlots;

        // 将相邻的 reserved 槽位合并为连续区间，计算百分比位置，并记录起止时间与文本标签
        const segCount = seatSlots.length; // 30
        const segPercent = 100 / segCount;
        const ranges = [];
        let i = 0;
        while (i < segCount) {
          if (seatSlots[i].status === 'reserved') {
            const startIdx = i;
            while (i < segCount && seatSlots[i].status === 'reserved') i++;
            const endIdx = i - 1;
            const left = startIdx * segPercent;
            const width = (endIdx - startIdx + 1) * segPercent;
            const begin = new Date(dayStart.getTime() + startIdx * 30 * 60 * 1000);
            const end = new Date(dayStart.getTime() + (endIdx + 1) * 30 * 60 * 1000);
            const fmt = (d) => `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
            ranges.push({ key: `${seatId}-${startIdx}-${endIdx}`, left, width, begin, end, label: `${fmt(begin)}-${fmt(end)}` });
          } else {
            i++;
          }
        }
        reservedRanges[seatId] = ranges;

        // 计算可预约的连续区间（绿色区间），并记录对应的起止时间
        const freeList = [];
        let j = 0;
        while (j < segCount) {
          if (seatSlots[j].status === 'free') {
            const startIdx = j;
            while (j < segCount && seatSlots[j].status === 'free') j++;
            const endIdx = j - 1; // 包含 endIdx
            const left = startIdx * segPercent;
            const width = (endIdx - startIdx + 1) * segPercent;
            const begin = new Date(dayStart.getTime() + startIdx * 30 * 60 * 1000);
            const end = new Date(dayStart.getTime() + (endIdx + 1) * 30 * 60 * 1000); // 结束为下一槽起点
            const fmt = (d) => `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
            freeList.push({
              key: `${seatId}-free-${startIdx}-${endIdx}`,
              left,
              width,
              begin,
              end,
              label: `${fmt(begin)}-${fmt(end)}`
            });
          } else {
            j++;
          }
        }
        freeRanges[seatId] = freeList;
      });
      this.seatSchedules = schedules;
      this.seatReservedRanges = reservedRanges;
      this.seatFreeRanges = freeRanges;
    },
    onBarHover(evt, seatId) {
      if (!this.form.datetime) return;
      const bar = evt.currentTarget;
      const rect = bar.getBoundingClientRect();
      const x = evt.clientX - rect.left;
      const percent = Math.max(0, Math.min(100, (x / rect.width) * 100));
      // 在可预约区间中寻找包含当前光标位置的那一段绿色区间
      const ranges = this.seatFreeRanges[seatId] || [];
      const hit = ranges.find(r => percent >= r.left && percent <= (r.left + r.width));
      if (hit) {
        // 绿色区间：显示可预约时段
        const center = hit.left + hit.width / 2;
        this.barTooltip = { seatId, left: center, label: hit.label };
        return;
      }
      // 如果没有命中绿色区间，再尝试命中已预约（红色）区间并显示其时间
      const reserved = this.seatReservedRanges[seatId] || [];
      const hitReserved = reserved.find(r => percent >= r.left && percent <= (r.left + r.width));
      if (hitReserved) {
        const center = hitReserved.left + hitReserved.width / 2;
        this.barTooltip = { seatId, left: center, label: hitReserved.label };
        return;
      }
      // 未命中任何区间时不显示提示
      this.barTooltip = null;
    },
    onBarLeave() {
      this.barTooltip = null;
    },
    seatcelect(seatId, seatNumber, index){
      // 使用 seatId 作为后端需要的整数标识，同时保留 seatNumber 供 UI 显示
      this.seatselectnum = seatId;
      this.roomseatcelect = Array(this.seatnum[this.currentIndex-1]).fill(0);
      const seatIdx = Number(seatNumber.toString().replace(/[^0-9]/g, '')) - 1;
      const flagAtIndex = (Number.isInteger(seatIdx) && seatIdx >=0 && seatIdx < this.roomseatflag.length) ? this.roomseatflag[seatIdx] : this.roomseatflag[index];
      if(flagAtIndex === 2)
      {
        this.$message.error('该时间段已被预约满，请选择其他座位');
        return;
      }
      else if(flagAtIndex === 1)
      {
        this.$message.error('该座位在当前时间段和他人预约时间重合，请调整时间');
        return;
      }
      else{
        this.$message.success(`已经成功选择座位 ${seatNumber}`);
        for(let i=0; i<this.roomseatcelect.length; i++)
        {
          this.roomseatcelect[i] = (i === index) ? 1 : 0;
        }
      }
    }
  }
};
</script>

<style scoped>
.image {
  width: 100%;
  /* 图片宽度充满容器 */
  height: 275px
}
.homeimage{
  width: 80%;
  height: 600px;
}

.seat-image {
  width: 90px;
  height: 90px;
  object-fit: contain;
  display: block;
  margin: 4px auto 2px;
  position: relative;
  z-index: 1;
}

.label-row {
  margin-bottom: 10px;
}

.label {
  display: inline-block;
  width: 120px; /* 根据需求调整宽度 */
  text-align: right;
  margin-right: 10px;
}
body {
  font-family: 'Arial', sans-serif;
}

.container {
  width: 90%;
  margin: auto;
  background: url("./bg2.svg");
  background-size: cover; /* 确保背景图覆盖整个容器 */

}

.bold {
  font-weight: bold;
  font-size: larger;
}

header {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #3e5ebd;
  color: white;
}

footer {
  background: #3e5ebd;
  color: white;
  text-align: center;
  padding: 7px 0;
  margin-top: 20px
}

.footer-detail {
  background-color: #ffffff;
  text-align: right;
}

/* 表单和按钮样式 */

.el-menu-horizontal-demo {
  display: flex;
}

.el-menu-item {
  flex-grow: 1;
}

.box-book-card {
  width: calc(25% - 15px);
  margin-bottom: 15px;
  margin-left: 10px;
  background-image: url("../assets/bookingback.png");
  background-size: 100% 100%; /* 背景图片尺寸为卡片的100% */
  background-position: center; /* 背景图片居中显示 */
}
.box-book-card :deep(.el-card__header) {
  padding-top: 40px; /* 增加顶部内边距，让标题下移 */
  padding-left: 30px;
  padding-right: 30px;
  padding-bottom: 4px;
  border-bottom: none; /* 与贴纸背景融合，去掉分隔线 */
}
.header-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 6px; /* 恢复原先的向下偏移效果 */
}
.status-tag {
  margin-left: 8px;
}
.box-card {
  width: calc(25% - 15px);
  margin-bottom: 15px;
  margin-left: 10px;
}
.card-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.title{ text-align:center; margin:0; padding:10px 0; color: #fff }

.booking-layout {
  display: flex;
  gap: 16px;
}
.booking-left,
.booking-right {
  flex: 1;
}
.booking-right {
  max-height: 420px;
  overflow-y: auto;
  padding: 4px 8px;
  background: #ffffff;
  border-radius: 4px;
}
.booking-left {
  max-height: 420px;
  overflow-y: auto;
  padding-top: 6px;
}
.seat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
  margin-top: 8px;
}
.seat-btn {
  width: 100%;
  padding: 6px 0;
  position: relative;
  height: 110px;
}
.seat-caption {
  position: absolute;
  top: 4px;
  left: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #2c3e50;
  text-shadow: 0 1px 2px rgba(255,255,255,0.9);
  z-index: 2;
  pointer-events: none;
}

.schedule-container {
  margin-top: 8px;
}
.schedule-row {
  display: flex;
  align-items: center;
  margin: 4px 0;
}
.schedule-seat-label {
  width: 90px;
  text-align: right;
  margin-right: 8px;
  font-weight: bold;
}
.schedule-bar {
  position: relative;
  height: 16px;
  width: 100%;
  background-color: #4CAF50; /* 绿色整体底条 */
  border-radius: 8px;
}
.bar-reserved {
  position: absolute;
  top: 0;
  height: 100%;
  background-color: #E53935; /* 红色覆盖已预约区间 */
  border-radius: 8px;
}
.bar-tooltip {
  position: absolute;
  top: -24px;
  transform: translateX(-50%);
  background: #ffffff;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 11px;
  white-space: nowrap;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}
.schedule-legend {
  margin-top: 6px;
}
.legend-box {
  display: inline-block;
  width: 12px;
  height: 12px;
  margin-right: 6px;
}
.legend-box.free { background-color: #4CAF50; }
.legend-box.reserved { background-color: #E53935; }

/* 预约控件居中加长 */
.booking-controls {
  width: 70%;
  margin: 0 auto 12px;
}
.booking-controls .label-row {
  display: flex;
  align-items: center;
}
.booking-controls .el-input,
.booking-controls .el-date-editor,
.booking-controls .el-time-picker {
  width: 100%;
}
.label {
  width: 140px;
}

</style>
