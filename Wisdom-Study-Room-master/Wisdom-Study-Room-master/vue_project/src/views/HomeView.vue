<template>
  <div class="container">
    <el-container>

      <el-header>
        <div class="header-content">
          <h1>智慧自习室</h1>
          <div class="user-area">
            <el-popover
              placement="bottom"
              title="用户信息"
              width="180"
              :show-arrow="false"
              v-model="showPopover"
              @mouseenter="showPopover = true"
              @mouseleave="showPopover = false"
            >
              <div>
                <h4 style="margin:4px 0">昵称: {{userData.userName}}</h4>
                <h4 style="margin:4px 0">账号: {{userData.userAccount}}</h4>
                <div style="display:flex;gap:8px;margin-top:8px">
                  <el-button type="success" @click="modifyUseDialogTab = true" size="mini">修改昵称</el-button>
                  <el-button type="danger" @click="Logout" size="mini">退出</el-button>
                </div>
              </div>
              <template #reference>
                <el-avatar class="header-avatar" style="cursor: pointer;">
                  <img src="../assets/portrait.png" alt="用户头像">
                </el-avatar>
              </template>
            </el-popover>
          </div>
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
              <div style="margin-top: 10px">
                <span class="bold">预约信息{{ index + 1 }}</span>
              </div>
            </template>
            <div style="margin-top: -18px">
              <p>日期: {{ booking.reserveTimeBegin.split(' ')[0] }}</p>
              <p>起始时间: {{ booking.reserveTimeBegin.split(' ')[1] }}</p>
              <p>结束时间: {{booking.reserveTimeEnd.split(' ')[1]  }}</p>
              <p>自习室: {{ booking.reserveRoomId }}号</p>
              <p>座位号: {{ booking.reserveSeatNumber }}号</p>
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
        <p>© 2024 智慧自习室</p>
      </el-footer>
    </el-container>

    <el-dialog title="预约详情" v-model="Reservedetail" width="420px" style="font-weight: bold;color: black">
      <div style="margin-top: -25px">
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约ID:{{this.bookingnum.reserveId}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约账号:{{this.bookingnum.reserveUserAccount}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约开始时间:{{this.bookingnum.reserveTimeBegin}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约结束时间:{{this.bookingnum.reserveTimeEnd}}</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约房间号:{{this.bookingnum.reserveRoomId}}号</p>
      <p style="margin-left: 65px;font-size: 16px; color: black; font-weight: normal; text-align: left;">预约座位号:{{this.bookingnum.reserveSeatNumber}}号</p>
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

    <el-dialog title="自习室预约" v-model="dialogTableVisible" width="500px" style="font-weight: bold">
      <el-row class="label-row">
        <span class="label">预约日期: </span>
        <el-date-picker
          v-model="form.datetime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择日期时间"
          format="yyyy-MM-dd"
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

      <p style="font-weight: bold;color: black">自习室座位图</p>

      <el-row>
        <el-col v-for="(seat,index) in seatsByRoom[currentIndex]" :key="index" :span="6">
          <el-button type="text" @click="seatcelect(seat.seatNumber,index)">
            <img :src="
            roomseatflag[index] === 0 && roomseatcelect[index] === 0? seatstateImg[index%2]
          : roomseatflag[index] === 1 && roomseatcelect[index] === 0? seatstateImg[index%2+2]
          : roomseatflag[index] === 2 && roomseatcelect[index] === 0? seatstateImg[index%2+4]
          : seatstateImg[index%2+6]"
            class="seat-image" alt="图片加载中">
          </el-button>
        </el-col>
      </el-row>

          <el-footer class="footer-detail">
            <el-button @click="dialogTableVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitForm">预 约</el-button>
          </el-footer>
    </el-dialog>

    <el-dialog title="自习室座位分布" v-model="ModifyTable" width="450px" style="font-weight: bold">

      <el-row class="label-row">
        <span class="label">预约日期: </span>
        <el-date-picker
          v-model="form.datetime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择日期时间"
          format="yyyy-MM-dd"
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

      <p style="font-weight: bold;color: black">自习室座位图</p>

      <el-row>
        <el-col v-for="(seat,index) in seatsByRoom[currentIndex]" :key="index" :span="6">
          <el-button type="text" @click="seatcelect(seat.seatNumber,index)">
            <img :src="
            roomseatflag[index] === 0 && roomseatcelect[index] === 0? seatstateImg[index%2]
          : roomseatflag[index] === 1 && roomseatcelect[index] === 0? seatstateImg[index%2+2]
          : roomseatflag[index] === 2 && roomseatcelect[index] === 0? seatstateImg[index%2+4]
          : seatstateImg[index%2+6]"
                 class="seat-image" alt="图片加载中">
          </el-button>
        </el-col>
      </el-row>

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
      // 解析 this.form.datetime 字符串为 Date 对象
      const datetimeStr = this.form.datetime;
      const datetimeParts = datetimeStr.split('-'); // 假设格式为 "YYYY-MM-DD"
      const datetime = new Date(
        parseInt(datetimeParts[0], 10), // 年份
        parseInt(datetimeParts[1], 10) - 1, // 月份从 0 开始计数
        parseInt(datetimeParts[2], 10) // 日
      )
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
          timeSignIn: currentTime.toLocaleString().replace(/\//g, '-')
        }).then(response => {
          if (response.data.code === 200) {
            this.$message.success('签到成功！');
            this.reservestate = '已签到'
            this.checkedIn = true
            this.checkedOut = false
            this.bookingnum.timeSignIn = currentTime.toLocaleString().replace(/\//g, '-');
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
        timeSignOut: currentTime.toLocaleString().replace(/\//g, '-')
      }).then(response => {
        if (response.data.code === 200) {
          this.$message.success('签退成功！');
          this.checkedOut = true
          this.reservestate = '已签退'
          this.bookingnum.timeSignOut = currentTime.toLocaleString().replace(/\//g, '-');
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
        axios.post('reserve/add', {
          reserveUserAccount : getUserAccount,
          reserveSeatNumber: this.seatselectnum,
          reserveRoomId: this.currentIndex,
          reserveTimeBegin: this.form.datetime+' ' +this.form.startTime+':00',
          reserveTimeEnd: this.form.datetime+' '+this.form.endTime+':00'
        }).then(response => {
          if (response.data.code === 200) {
            this.$message.success('预约成功！');
            this.dialogTableVisible = false
          } else {
            this.$message.error(response.data.msg);
          }
        }).catch(error => {
          console.error('请求错误:', error);
          this.$message.error('网络错误，请稍后重试');
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
        const requestBody = {
          reserveId: this.ModifyreserveId,
          reserveUserName: getUserAccount,
          reserveSeatNumber: this.seatselectnum,
          reserveRoomId: this.ModifyroomId,
          reserveTimeBegin: this.form.datetime+' '+this.form.startTime+':00',
          reserveTimeEnd: this.form.datetime+' '+this.form.endTime+':00'
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
      }
    },
    seatcelect(seatnum,index){
      this.seatselectnum = seatnum
      this.roomseatcelect = Array(this.seatnum[this.currentIndex-1]).fill(0);
      if(this.roomseatflag[seatnum-1] === 2)
      {
        this.$message.error('该时间段已被预约满，请选择其他座位');
      }
      else if(this.roomseatflag[seatnum-1] === 1)
      {
        this.$message.error('该座位在当前时间段和他人预约时间重合，请调整时间');
      }
      else{
        this.$message.success(`已经成功选择座位 ${this.seatselectnum}`);
        for(let i=0; i<this.roomseatcelect.length; i++)
        {
          if(i === index)
          {
            this.roomseatcelect[i] = 1
          }
          else {
            this.roomseatcelect[i] = 0
          }
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
  width: 100px;
  /* 图片宽度充满容器 */
  height: 100px
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

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  color: white;
}

.user-area {
  display: flex;
  align-items: center;
}

.header-avatar img {
  width: 36px;
  height: 36px;
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

</style>
