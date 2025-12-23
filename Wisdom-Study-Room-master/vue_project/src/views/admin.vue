<template>
  <div id="app">
    <!-- 全局悬浮 tooltip，fixed 定位确保不被表格裁剪 -->
    <div v-if="adminBarTooltipGlobal.visible" class="admin-floating-tooltip" :style="{ left: adminBarTooltipGlobal.left + 'px', top: adminBarTooltipGlobal.top + 'px' }">
      {{ adminBarTooltipGlobal.label }}
    </div>
    <el-container>
      <el-header style="display: grid; grid-template-columns: auto auto; align-items: center;">
        <div style="display: flex; align-items: center;">
          <img src="../assets/logo.png" alt="logo" class="logo-image">
          <h3 style="margin-left: 10px;">智慧自习室</h3>
        </div>
        <el-button type="danger" style="max-width: 120px; justify-self: end; margin-right: 20px;" @click="Logout" class="delete-btn">退出</el-button>
      </el-header>

      <el-container class="sidebar" >
        <!--侧边栏-->
        <el-aside width="200px" >
          <el-menu class="el-menu-vertical-demo" default-active="0" style="height: 100%;flex-grow: 1;margin: 0; padding: 0;" background-color="#304156" text-color="#fff" active-text-color="#409EFF">
            <el-menu-item index="0" @click="changeTab('home');" style="text-align: left;">
              <el-icon><HomeFilled /></el-icon>
              <template #title><span>主页</span></template>
            </el-menu-item>
            <el-menu-item index="1" @click="changeTab('room'); loadRooms()" style="text-align: left;">
              <el-icon><House /></el-icon>
              <template #title><span>自习室管理</span></template>
            </el-menu-item>
            <el-menu-item index="2" @click="changeTab('seat'); loadSeats()" style="text-align: left;">
              <el-icon><Menu /></el-icon>
              <template #title><span>座位管理</span></template>
            </el-menu-item>
            <el-menu-item index="3" @click="changeTab('reserve'); loadReserve()" style="text-align: left;">
              <el-icon><Tickets /></el-icon>
              <template #title><span>预约管理</span></template>
            </el-menu-item>
            <el-menu-item index="4" @click="changeTab('user'); loadUsers()" style="text-align: left;">
              <el-icon><User /></el-icon>
              <template #title><span>用户管理</span></template>
            </el-menu-item>
            <el-menu-item index="5" @click="changeTab('violation'); loadViolations()" style="text-align: left;">
              <el-icon><Warning /></el-icon>
              <template #title><span>违规信息</span></template>
            </el-menu-item>

          </el-menu>
        </el-aside>

        <!-- 主体 -->
        <el-main style="min-height: 100vh;">
          <el-card class="box-card" style="margin-bottom: 12px;">
            <div class="card-container">
              <img src="../assets/portrait.png" alt="logo" class="ava">
              <div class="admin-info">
                <h3>{{changelab()}}</h3>
                <div class="weather">
                  <el-icon><Pouring /></el-icon>
                  <span>今日阴转小雨，22℃ - 32℃，出门记得带伞哦。</span>
                </div>
              </div>
            </div>

          </el-card>
           <el-carousel :interval="3000" arrow="always" height="80vh"  v-show="currentTab === 'home'">
                <el-carousel-item v-for="room in 3" :key="room">
                  <img :src="`./img/room${room}.jpg`" alt="自习室图片" class="homeimage">
                </el-carousel-item>
           </el-carousel>
          <!-- 预约信息展示框 -->
          <el-card class="box-card" style="margin-bottom: 12px;" v-show="currentTab === 'reserve'">
            <!-- 搜索框 -->
            <el-input v-model="reserveSearch" placeholder="请输入账号或预约Id进行搜索" style="margin-bottom: 12px;"></el-input>
            <!-- 添加预约信息按钮 -->
            <div style="display: flex;">
              <el-button type="primary"  @click="showAddReserveDialog" class="add-btn" >
                <el-icon><DocumentAdd /></el-icon>添加预约</el-button>
            </div>
            <el-table :data="filteredReserveData"
                      :sortable="true"
                      @sort-change="handleReserveSortChange"
                      stripe style="width: 100%; ">
              <el-table-column prop="reserveId" label="预约Id" width="110px" sortable>
              </el-table-column>
              <el-table-column prop="reserveUserAccount" label="预约账号" width="110px" sortable>
              </el-table-column>
              <el-table-column label="自习室" width="140px" sortable>
                <template v-slot="scope">
                  <span>自习室：{{ scope.row.reserveRoomName || scope.row.reserveRoomId }}</span>
                </template>
              </el-table-column>
              <el-table-column label="座位号" width="140px" sortable>
                <template v-slot="scope">
                  <span>座位号：{{ scope.row.reserveSeatNumber }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="reserveTimeBegin" label="预约起始时间" sortable>
              </el-table-column>
              <el-table-column prop="reserveTimeEnd" label="预约结束时间" sortable>
              </el-table-column>
              <el-table-column prop="timeSignIn" label="签到时间" sortable>
              </el-table-column>
              <el-table-column prop="timeSignOut" label="签退时间" sortable>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template v-slot="scope">
                  <div class="table-ops">
                    <el-button type="primary" plain @click="editReserve(scope.row)" class="edit-btn">
                      <el-icon><Edit /></el-icon>编辑</el-button>
                    <el-button type="danger" plain @click="deleteReserve(scope)" class="delete-btn">
                      <el-icon><Delete /></el-icon>删除</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;">
              <el-pagination background layout="prev, pager, next" :total="reserveData.length" :page-size="reservePageSize" :current-page="reserveCurrentPage" @current-change="handleReserveCurrentChange"></el-pagination>
            </div>
          </el-card>

          <!-- 自习室信息展示框 -->
          <el-card v-show="currentTab === 'room'">
            <!-- 搜索框 -->
            <el-input v-model="roomSearch" placeholder="请输入自习室名称进行搜索" style="margin-bottom: 12px;"></el-input>
            <!-- 添加自习室按钮 -->
            <div style="display: flex;">
              <el-button type="primary"  @click="showAddRoomDialog" class="add-btn" >
                <el-icon><DocumentAdd /></el-icon>添加自习室</el-button>
            </div>
            <!-- 自习室列表 -->
            <el-table :data="filteredRoomData"
                      :sortable="true"
                      @sort-change="handleRoomSortChange"
                      stripe style="width: 100%; ">
              <el-table-column prop="roomId" label="自习室id" sortable>
              </el-table-column>
              <el-table-column prop="roomName" label="自习室名称" sortable>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template v-slot="scope">
                  <div class="table-ops">
                    <el-button type="primary" plain @click="editRoom(scope.row)" class="edit-btn">
                      <el-icon><Edit /></el-icon>编辑</el-button>
                    <el-button type="danger" plain @click="deleteRoom(scope)" class="delete-btn">
                      <el-icon><Delete /></el-icon>删除</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;">
              <el-pagination background layout="prev, pager, next" :total="roomData.length" :page-size="roomPageSize" :current-page="roomCurrentPage" @current-change="handleRoomCurrentChange"></el-pagination>
            </div>
          </el-card>

          <!-- 座位信息展示框 -->
          <el-card v-show="currentTab === 'seat'">
            <!-- 搜索框 -->
            <el-input v-model="seatSearch" placeholder="请输入自习室名称或者座位编号进行搜索" style="margin-bottom: 12px;"></el-input>
            <!-- 操作按钮栏 -->
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
              <el-button type="primary" @click="showAddSeatDialog" class="add-btn">
                <el-icon><DocumentAdd /></el-icon>添加座位
              </el-button>
              <span style="margin-left: auto; display: flex; align-items: center; gap: 8px;">
                <label style="font-weight: 600;">查看日期:</label>
                <el-date-picker
                  v-model="scheduleDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  @change="handleScheduleDateChange"
                  style="width: 180px;">
                </el-date-picker>
              </span>
            </div>
            <!-- 座位列表 -->
            <el-table :data="filteredSeatData"
                      :sortable="true"
                      @sort-change="handleSeatSortChange"
                      stripe 
                      style="width: 100%; overflow: visible;"
                      :cell-style="{overflow: 'visible'}"
                      :header-cell-style="{overflow: 'visible'}">
              <el-table-column prop="seatId" label="座位Id" sortable></el-table-column>
              <el-table-column prop="seatNumber" label="座位编号" sortable></el-table-column>
              <el-table-column prop="roomName" label="所在自习室" sortable></el-table-column>
              <!-- 新增：当天预约情况可视化条 -->
              <el-table-column label="当天预约情况" min-width="260">
                <template v-slot="scope">
                  <div class="schedule-bar-wrapper">
                    <div class="schedule-bar in-table"
                         @mousemove="onAdminBarHover($event, scope.row.seatId)"
                         @mouseleave="onAdminBarLeave">
                      <div v-for="r in (seatReservedRanges[scope.row.seatId] || [])"
                           :key="r.key"
                           class="bar-reserved"
                           :style="{ left: r.left + '%', width: r.width + '%' }"></div>
                    </div>
                    <div v-if="adminBarTooltip && adminBarTooltip.seatId === scope.row.seatId"
                         class="bar-tooltip-overlay"
                         :style="{ left: adminBarTooltip.left + '%'}">{{ adminBarTooltip.label }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template v-slot="scope">
                  <div class="table-ops">
                    <el-button type="primary" plain @click="editSeat(scope.row)" class="edit-btn">
                      <el-icon><Edit /></el-icon>编辑
                    </el-button>
                    <el-button type="danger" plain @click="deleteSeat(scope)" class="delete-btn">
                      <el-icon><Delete /></el-icon>删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;">
              <el-pagination background layout="prev, pager, next" :total="this.seatData.length" :page-size="seatPageSize" :current-page="seatCurrentPage" @current-change="handleSeatCurrentChange"></el-pagination>
            </div>
          </el-card>

          <!-- 用户信息展示框 -->
          <el-card class="box-card" style="margin-bottom: 12px;" v-show="currentTab === 'user'">
            <!-- 搜索框 -->
            <el-input v-model="userSearch" placeholder="请输入账号或用户名进行搜索" style="margin-bottom: 12px;"></el-input>
            <!-- 添加用户信息按钮 -->
            <div style="display: flex;">
              <el-button type="primary"  @click="showAddUserDialog" class="add-btn" >
                <el-icon><DocumentAdd /></el-icon>添加用户</el-button>
            </div>
            <el-table :data="filteredUserData"
                      :sortable="true"
                      @sort-change="handleUserSortChange"
                      stripe style="width: 100%; ">
              <el-table-column prop="userId" label="用户Id" sortable>
              </el-table-column>
              <el-table-column prop="userAccount" label="账号"  sortable>
              </el-table-column>
              <el-table-column prop="userName" label="用户名" sortable>
              </el-table-column>
              <el-table-column prop="userPassword" label="密码">
                <template v-slot="scope">
                   <span v-if="scope.row.showPassword" style="cursor: pointer;" @click="togglePassword(scope.row)">
                    {{ scope.row.userPassword }}
                   </span>
                  <span v-else style="cursor: pointer;" @click="togglePassword(scope.row)">
                     &#9679;&#9679;&#9679;&#9679;&#9679;&#9679; <!-- 黑色圆点的HTML实体 -->
                   </span>
                </template>
              </el-table-column>
              <el-table-column prop="userPrivilege" label="权限">
              </el-table-column>
              <el-table-column prop="userIllegal" label="违规次数" sortable>
              </el-table-column>
              <el-table-column prop="userIllegalState" label="是否封禁" sortable>
              </el-table-column>
              <el-table-column prop="userIllegalDate" label="封禁截至日期" sortable>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template v-slot="scope">
                  <div class="table-ops">
                    <el-button type="primary" plain @click="editUser(scope.row)" class="edit-btn">
                      <el-icon><Edit /></el-icon>编辑</el-button>
                    <el-button type="danger" plain @click="deleteUser(scope)" class="delete-btn">
                      <el-icon><Delete /></el-icon>删除</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;">
              <el-pagination background layout="prev, pager, next" :total="userData.length" :page-size="userPageSize" :current-page="userCurrentPage" @current-change="handleUserCurrentChange"></el-pagination>
            </div>
          </el-card>

          <!-- 违规信息展示框 -->
          <el-card class="box-card" style="margin-bottom: 12px;" v-show="currentTab === 'violation'">
            <!-- 搜索框 -->
            <el-input v-model="violationSearch" placeholder="请输入账号进行搜索" style="margin-bottom: 12px;"></el-input>
            <!-- 添加违规信息按钮 -->
            <div style="display: flex;">
              <el-button type="primary" @click="showAddViolationDialog" class="add-btn">
                <el-icon><DocumentAdd /></el-icon>添加违规信息
              </el-button>
            </div>
            <el-table :data="filteredViolationData"
                      :sortable="true"
                      @sort-change="handleViolationSortChange"
                      stripe style="width: 100%; ">
              <el-table-column prop="logId" label="违规Id" sortable>
              </el-table-column>
              <el-table-column prop="userAccount" label="账号" sortable>
              </el-table-column>
              <el-table-column prop="logState" label="违规信息" sortable>
              </el-table-column>
              <el-table-column prop="logTime" label="违规时间" sortable>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template v-slot="scope">
                  <div class="table-ops">
                    <!-- 编辑违规信息按钮 -->
                    <el-button type="primary" plain @click="editViolation(scope.row)" class="edit-btn">
                      <el-icon><Edit /></el-icon>编辑
                    </el-button>
                    <!-- 删除违规信息按钮 -->
                    <el-button type="danger" plain @click="deleteViolation(scope)" class="delete-btn">
                      <el-icon><Delete /></el-icon>删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div style="text-align: center;">
              <el-pagination background layout="prev, pager, next" :total="violationData.length" :page-size="violationPageSize" :current-page="violationCurrentPage" @current-change="handleViolationCurrentChange"></el-pagination>
            </div>
          </el-card>

          <!-- 添加违规信息对话框 -->
          <el-dialog title="添加违规信息" v-model="addViolationDialogVisible" width="600px">
            <el-form :model="newViolation" label-width="100px">
              <el-form-item label="账号">
                <el-input v-model="newViolation.userAccount" style="margin-bottom: 15px"></el-input>
              </el-form-item>
              <el-form-item label="违规信息">
                <el-input v-model="newViolation.logState" style="margin-bottom: 15px"></el-input>
              </el-form-item>
              <!-- 其他表单项... -->
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="cancelAddViolation" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消
                </el-button>
                <el-button type="primary" @click="saveNewViolation" class="save-btn">
                  <el-icon><Check /></el-icon>保存
                </el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 编辑违规信息面板 -->
          <el-dialog title="编辑违规信息" v-model="editViolationDialogVisible" width="600px">
            <!-- 编辑表单 -->
            <el-form ref="editRoomForm" :model="editViolationForm" label-width="80px">
              <el-form-item label="违规Id" style="margin-bottom: 15px">
                <el-input v-model="editViolationForm.logId"></el-input>
              </el-form-item>
              <el-form-item label="账号" style="margin-bottom: 15px">
                <el-input v-model="editViolationForm.userAccount"></el-input>
              </el-form-item>
              <el-form-item label="违规信息" style="margin-bottom: 15px">
                <el-input v-model="editViolationForm.logState"></el-input>
              </el-form-item>
              <el-form-item label="违规时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="editViolationForm.logTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择违规时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
            </el-form>
            <!-- 用户操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="editViolationDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveEditedViolation" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 删除违规确认对话框 -->
          <el-dialog title="提示" v-model="deleteViolationDialogVisible" width="30%">
            <span>确认删除该违规信息吗？</span>
            <!-- 操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="deleteViolationDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="danger" @click="confirmDeleteViolation" class="delete-btn">
                  <el-icon><Delete /></el-icon>删除</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 编辑自习室信息面板 -->
          <el-dialog title="编辑自习室信息" v-model="editRoomDialogVisible">
            <!-- 编辑表单 -->
            <el-form ref="editRoomForm" :model="editRoomForm" label-width="80px">
              <el-form-item label="名称">
                <el-input v-model="editRoomForm.roomName"></el-input>
              </el-form-item>
              <!-- 其他表单项 -->
            </el-form>
            <!-- 自习室操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="editRoomDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveEditedRoom" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 删除自习室确认对话框 -->
          <el-dialog title="提示" v-model="deleteRoomDialogVisible" width="30%">
            <span>确认删除该自习室吗？</span>
            <!-- 操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="deleteRoomDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="danger" @click="confirmDeleteRoom" class="delete-btn">
                  <el-icon><Delete /></el-icon>删除</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 添加自习室对话框 -->
          <el-dialog title="添加自习室" v-model="addRoomDialogVisible" width="600px">
            <el-form :model="newRoom" label-width="100px">
              <el-form-item label="新自习室名称" style="margin-bottom: 15px">
                <el-input v-model="newRoom.roomName" placeholder="请输入自习室名称"></el-input>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="cancelAddRoom" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveNewRoom" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 编辑座位信息面板 -->
          <el-dialog title="编辑座位信息" v-model="editSeatDialogVisible" width="600px">
            <!-- 编辑表单 -->
            <el-form ref="editSeatForm" :model="editSeatForm" label-width="100px">
              <el-form-item label="座位编号"  style="margin-bottom: 15px">
                <el-input v-model="editSeatForm.seatNumber"></el-input>
              </el-form-item>
              <el-form-item label="所属自习室" style="margin-bottom: 15px">
                <el-select v-model="editSeatForm.roomId" placeholder="请选择自习室" style="width: 100%">
                  <el-option v-for="room in roomData" :key="room.roomId" :label="room.roomName" :value="room.roomId"></el-option>
                </el-select>
              </el-form-item>
              <!-- 其他表单项 -->
            </el-form>
            <!-- 座位操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="editSeatDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveEditedSeat" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 删除座位确认对话框 -->
          <el-dialog title="提示" v-model="deleteSeatDialogVisible" width="30%">
            <span>确认删除该座位吗？</span>
            <!-- 操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button tyoe="default" @click="deleteSeatDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="danger" @click="confirmDeleteSeat" class="delete-btn">
                  <el-icon><Delete /></el-icon>删除</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 添加座位对话框 -->
          <el-dialog title="添加座位" v-model="addSeatDialogVisible" width="600px">
            <el-form :model="newSeat" label-width="100px">
              <el-form-item label="座位编号" style="margin-bottom: 15px">
                <el-input v-model="newSeat.seatNumber" placeholder="请输入座位编号"></el-input>
              </el-form-item>
              <el-form-item label="所属自习室" style="margin-bottom: 15px">
                <el-select v-model="newSeat.roomId" placeholder="请选择自习室" style="width: 100%">
                  <el-option v-for="room in roomData" :key="room.roomId" :label="room.roomName" :value="room.roomId"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="cancelAddSeat" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveNewSeat" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 删除预约信息确认对话框 -->
          <el-dialog title="提示" v-model="deleteReserveDialogVisible" width="30%">
            <span>确认删除该预约吗？</span>
            <!-- 操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="deleteReserveDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="danger" @click="confirmDeleteReserve" class="delete-btn">
                  <el-icon><Delete /></el-icon>删除</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 编辑预约信息面板 -->
          <el-dialog title="编辑预约信息" v-model="editReserveDialogVisible">
            <!-- 编辑表单 -->
            <el-form ref="editReserveForm" :model="editReserveForm" label-width="80px">
              <el-form-item label="预约账号" style="margin-bottom: 15px" >
                <el-input v-model="editReserveForm.reserveUserAccount" ></el-input>
              </el-form-item>
              <el-form-item label="自习室名" style="margin-bottom: 15px">
                <el-select v-model="editReserveForm.reserveRoomName" placeholder="请选择自习室" style="width: 100%">
                  <el-option
                    v-for="room in roomData"
                    :key="room.roomId"
                    :label="room.roomName"
                    :value="room.roomName">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="座位号" style="margin-bottom: 15px">
                <el-input v-model="editReserveForm.reserveSeatNumber"></el-input>
              </el-form-item>
              <el-form-item label="起始时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="editReserveForm.reserveTimeBegin"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择日期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="结束时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="editReserveForm.reserveTimeEnd"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择日期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
              <!-- 其他表单项 -->
            </el-form>
            <!-- 自习室操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="editReserveDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveEditedReserve" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 添加预约信息对话框 -->
          <el-dialog title="添加预约" v-model="addReserveDialogVisible" width="600px">
            <el-form :model="newReserve" label-width="80px">
              <el-form-item label="预约账号" style="margin-bottom: 15px">
                <el-input v-model="newReserve.reserveUserAccount" placeholder="请输入用户账号"></el-input>
              </el-form-item>
              <el-form-item label="自习室名" style="margin-bottom: 15px">
                <el-select v-model="newReserve.reserveRoomId" placeholder="请选择自习室" style="width: 100%">
                  <el-option
                    v-for="room in roomData"
                    :key="room.roomId"
                    :label="room.roomName"
                    :value="room.roomId">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="座位号" style="margin-bottom: 15px">
                <el-input v-model="newReserve.reserveSeatNumber" placeholder="请输入座位号"></el-input>
              </el-form-item>
              <el-form-item label="起始时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="newReserve.reserveTimeBegin"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择日期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="结束时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="newReserve.reserveTimeEnd"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择日期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="cancelAddReserve" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveNewReserve" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 编辑用户信息面板 -->
          <el-dialog title="编辑用户信息" v-model="editUserDialogVisible" width="600px">
            <!-- 编辑表单 -->
            <el-form ref="editRoomForm" :model="editUserForm" label-width="100px">
              <el-form-item label="用户名" style="margin-bottom: 15px">
                <el-input v-model="editUserForm.userName"></el-input>
              </el-form-item>
              <el-form-item label="密码" style="margin-bottom: 15px">
                <el-input v-model="editUserForm.userPassword"></el-input>
              </el-form-item>
              <el-form-item label="权限" style="margin-bottom: 15px">
                <el-input v-model="editUserForm.userPrivilege"></el-input>
              </el-form-item>
              <el-form-item label="是否封禁" style="margin-bottom: 15px">
                <el-select
                  v-model="editUserForm.userIllegalState"
                  placeholder="请选择封禁状态"
                  style="width: 100%"
                  @change="onIllegalStateChange"
                >
                  <el-option label="未封禁" :value="0" />
                  <el-option label="封禁" :value="1" />
                </el-select>
              </el-form-item>
              <el-form-item label="封禁截至时间" style="margin-bottom: 15px">
                <el-date-picker
                  v-model="editUserForm.userIllegalDate"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择日期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>
            </el-form>
            <!-- 用户操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="editUserDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveEditedUser" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 删除用户确认对话框 -->
          <el-dialog title="提示" v-model="deleteUserDialogVisible" width="30%">
            <span>确认删除该用户吗？</span>
            <!-- 操作按钮 -->
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="deleteUserDialogVisible = false" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="danger" @click="confirmDeleteUser" class="delete-btn">
                  <el-icon><Delete /></el-icon>删除</el-button>
              </div>
            </template>
          </el-dialog>

          <!-- 添加用户对话框 -->
          <el-dialog title="添加用户" v-model="addUserDialogVisible" width="600px">
            <el-form :model="newUser" label-width="80px">
              <el-form-item label="用户名" style="margin-bottom: 15px">
                <el-input v-model="newUser.userName" placeholder="请输入用户名"></el-input>
              </el-form-item>
              <el-form-item label="账号" style="margin-bottom: 15px">
                <el-input v-model="newUser.userAccount" placeholder="请输入账号"></el-input>
              </el-form-item>
              <el-form-item label="密码" style="margin-bottom: 15px">
                <el-input v-model="newUser.userPassword" placeholder="请输入密码"></el-input>
              </el-form-item>
              <el-form-item label="权限" style="margin-bottom: 15px">
                <el-input v-model="newUser.userPrivilege" placeholder="请输入权限"></el-input>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="cancelAddUser" class="cancel-btn">
                  <el-icon><Close /></el-icon>取消</el-button>
                <el-button type="primary" @click="saveNewUser" class="save-btn">
                  <el-icon><Check /></el-icon>保存</el-button>
              </div>
            </template>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";
import { ElMessageBox, ElMessage } from 'element-plus';
export default {
  data() {
    return {
      // 当前选中的标签
      currentTab: 'home',
      // 用户信息搜索关键字
      userSearch: '',
      // 自习室信息搜索关键字
      roomSearch: '',
      // 座位信息搜索关键字
      seatSearch: '',
      // 预约信息搜索关键字
      reserveSearch: '',
      //违规信息搜索关键字
      violationSearch: '',

      userCurrentPage: 1, // 当前用户管理页面
      roomCurrentPage: 1, // 当前自习室管理页面
      seatCurrentPage: 1, // 当前座位页面
      reserveCurrentPage : 1,// 当前预约管理页面
      violationCurrentPage : 1,//当前违规信息管理界面

      editUserDialogVisible: false, // 编辑用户信息对话框可见性
      deleteUserDialogVisible: false, // 删除用户对话框可见性
      editUserForm: {
        userId: '',
        userPassword: '',
        userName: '',
        userPrivilege:'',
        userIllegalState: 0,
        userIllegalDate:'',
        showPassword: false
      }, // 编辑用户表单数据
      editingUserIndex: -1, // 正在编辑的用户索引
      newUser: {
        userAccount: '',
        userPassword: '',
        userName: '',
        userPrivilege:''
      },
      // 添加用户信息对话框可见性
      addUserDialogVisible: false,
      defaultUserSortOptions: { prop: 'userId', order: 'ascending' }, // 默认排序选项

      editRoomDialogVisible: false, // 编辑自习室信息对话框可见性
      deleteRoomDialogVisible: false, // 删除自习室对话框可见性
      editRoomForm: { roomName: ''}, // 编辑自习室表单数据
      editingRoomIndex: -1, // 正在编辑的自习室索引
      addRoomDialogVisible: false, //添加自习室信息对话框可见性
      newRoom:{ roomName: ''},
      defaultRoomSortOptions: { prop: 'roomId', order: 'ascending' }, // 默认排序选项

      editSeatDialogVisible: false, // 编辑座位信息对话框可见性
      deleteSeatDialogVisible: false, // 删除座位对话框可见性
      editSeatForm: {
        seatId: '',
        seatNumber: '',
        roomId: ''
      }, // 编辑座位表单数据
      editingSeatIndex: -1, // 正在编辑的座位索引
      addSeatDialogVisible: false,// 添加座位对话框可见性
      newSeat: {
        seatNumber: '',
        roomId: ''
      },
      defaultSeatSortOptions: { prop: 'seatId', order: 'ascending' }, // 默认排序选项

      editReserveDialogVisible: false, // 编辑预约信息对话框可见性
      deleteReserveDialogVisible: false, // 删除预约信息对话框可见性
      // 编辑预约信息数据
      editReserveForm:{
        reserveId:'',
        reserveUserAccount: '',
        reserveSeatNumber: '',
        reserveRoomName: '',
        reserveTimeBegin: '',
        reserveTimeEnd: ''
      },
      // 正在编辑的预约信息索引
      editingReserveIndex: -1,
      // 添加预约信息对话框可见性
      addReserveDialogVisible: false,
      newReserve:{
        reserveUserAccount: '',
        reserveSeatNumber: '',
        reserveRoomId: '',
        reserveTimeBegin: '',
        reserveTimeEnd: ''
      },
      defaultReserveSortOptions: { prop: 'reserveId', order: 'ascending' }, // 默认排序选项

      //编辑违规信息数据
      editViolationDialogVisible: false,
      deleteViolationDialogVisible: false,
      editViolationForm: {
        logId:'',
        userAccount: '',
        logState:'',
        logTime:''
      },
      deletingViolationIndex: null, // 正在删除的违规信息索引
      addViolationDialogVisible: false, // 添加违规信息对话框的可见性
      // 用于表单的数据模型，结构应根据实际违规信息的结构定义
      defaultViolationSortOptions: { prop: 'logId', order: 'ascending' },
      newViolation: {
        userAccount: '',
        logState:''
      },

      // 每页显示的用户数据条数
      userPageSize: 8,
      // 每页显示的自习室数据条数
      roomPageSize: 8,
      // 每页显示的作为数据条数
      seatPageSize: 8,
      // 每页显示的预约数据条数
      reservePageSize: 8,
      // 每页显示的违规信息数据条数
      violationPageSize: 8,

      roomData: [

      ],

      seatData: [

      ],

      userData: [

      ],
      reserveData: [

      ],
      violationData: [

      ],

      // 座位预约可视化（管理员端）
      seatReservedRanges: {}, // seatId -> [{left,width,key}]
      seatFreeRanges: {},     // seatId -> [{left,width,begin,end,label}]
      adminBarTooltip: null,  // 旧的表内 tooltip 备用
      adminBarTooltipGlobal: { visible: false, left: 0, top: 0, label: '' }, // 固定在视口的 tooltip
      adminBarTooltipRaf: null,
      scheduleDate: '',       // 用于计算的日期（默认今天）

    };
  },
  computed: {
    // 过滤后的用户数据
    filteredUserData() {
      const start = (this.userCurrentPage - 1) * this.userPageSize;
      const end = start + this.userPageSize;
      const keyword = this.userSearch.toLowerCase(); // 将搜索关键字转换为小写
      const filteredData = this.userData.filter(user => {
        return user.userName.toLowerCase().includes(keyword) || user.userAccount.toLowerCase().includes(keyword)||
          user.userId.toString().toLowerCase().includes(keyword)
      })
      return this.sortData(filteredData,this.defaultReserveSortOptions).slice(start,end)
    },
    // 过滤后的预约数据
    filteredReserveData() {
      const start = (this.reserveCurrentPage - 1) * this.reservePageSize;
      const end = start + this.reservePageSize;
      const keyword = this.reserveSearch.toLowerCase(); // 将搜索关键字转换为小写
      const filteredData = this.reserveData.filter(reserve => {
        return reserve.reserveUserAccount.toLowerCase().includes(keyword) || reserve.reserveId.toString().toLowerCase().includes(keyword)
      })
      return this.sortData(filteredData,this.defaultReserveSortOptions).slice(start,end)
    },
    // 过滤后的自习室数据
    filteredRoomData() {
      const start = (this.roomCurrentPage - 1) * this.roomPageSize;
      const end = start + this.roomPageSize;
      const keyword = this.roomSearch.toLowerCase(); // 将搜索关键字转换为小写
      const filteredData =  this.roomData.filter(room => {
        return room.roomName.toLowerCase().includes(keyword) || room.roomId.toString().toLowerCase().includes((keyword))
      })
      return this.sortData(filteredData,this.defaultRoomSortOptions).slice(start,end)
    },
    // 过滤后的座位数据
    filteredSeatData() {
      const start = (this.seatCurrentPage - 1) * this.seatPageSize;
      const end = start + this.seatPageSize;
      const keyword = this.seatSearch.toLowerCase(); // 将搜索关键字转换为小写
      // 过滤数据
      const filteredData = this.seatData.filter(seat => {
        return seat.roomName.toLowerCase().includes(keyword) || seat.seatNumber.toString().toLowerCase().includes(keyword)
      })
      // 对过滤后的数据进行排序
      return this.sortData(filteredData, this.defaultSeatSortOptions).slice(start, end)
    },
    // 过滤后的违规信息数据
    filteredViolationData() {
      const start = (this.violationCurrentPage - 1) * this.violationPageSize;
      const end = start + this.violationPageSize;
      const keyword = this.violationSearch.toLowerCase();
      const filteredData = this.violationData.filter(violation => {
        return violation.userAccount.toLowerCase().includes(keyword);
      });
      return this.sortData(filteredData,this.defaultViolationSortOptions).slice(start,end)
    },
  },
  //页面打开时候，加载数据
  created() {
          this.loadReserve();
          this.loadRooms();
          this.loadSeats();
          this.loadUsers();
          this.loadViolations();
      // 初始化日期为今天
      const t = new Date();
      const y = t.getFullYear();
      const m = String(t.getMonth()+1).padStart(2,'0');
      const d = String(t.getDate()).padStart(2,'0');
      this.scheduleDate = `${y}-${m}-${d}`;
  },
  mounted() {
    document.title = this.$route.meta.title;
  },
  beforeUnmount() {
    if (this.adminBarTooltipRaf) cancelAnimationFrame(this.adminBarTooltipRaf);
  },
  methods: {
    //退出登录
    // 退出登录并显示确认弹窗
    Logout() {
      ElMessageBox.confirm('您确定要退出吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        sessionStorage.removeItem('userAccount');
        this.$router.push('/LoginView');
      }).catch(() => {});
    },
    // 排序数据
    sortData(data, { prop, order }) {
      return data.slice().sort((a, b) => {
        const fieldA = a[prop];
        const fieldB = b[prop];
        // 根据属性类型进行比较
        if (typeof fieldA === 'string' && typeof fieldB === 'string') {
          return order === 'ascending' ? fieldA.localeCompare(fieldB) : fieldB.localeCompare(fieldA);
        } else {
          return order === 'ascending' ? fieldA - fieldB : fieldB - fieldA;
        }
      });
    },
    // 处理排序变化
    handleSeatSortChange({ prop, order }) {
      this.defaultSeatSortOptions.prop = prop;
      this.defaultSeatSortOptions.order = order;
    },
    handleRoomSortChange({ prop, order }) {
      this.defaultRoomSortOptions.prop = prop;
      this.defaultRoomSortOptions.order = order;
    },
    handleUserSortChange({ prop, order }) {
      this.defaultUserSortOptions.prop = prop;
      this.defaultUserSortOptions.order = order;
    },
    handleReserveSortChange({ prop, order }) {
      this.defaultReserveSortOptions.prop = prop;
      this.defaultReserveSortOptions.order = order;
    },
    handleViolationSortChange({prop,order}){
      this.defaultViolationSortOptions.prop = prop;
      this.defaultViolationSortOptions.order = order;
    },
    // 自习室信息加载
    loadRooms() {
      const url = 'room/all';
      axios.get(url).then(response => {
        // 处理获取到的房间数据
        this.roomData = response.data.map(room => ({
          roomId: room.roomId,
          roomName: room.roomName
        }));
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      });
    },
    // 显示添加自习室对话框
    showAddRoomDialog() {
      this.addRoomDialogVisible = true;
    },
    // 取消添加自习室操作
    cancelAddRoom() {
      this.addRoomDialogVisible = false;
      this.newRoom = { roomName: '' }; // 重置表单数据
    },
    // 保存新添加的自习室信息
    saveNewRoom() {
      // 这里可以添加表单验证逻辑
      // 发送请求保存新添加的自习室信息
      axios.post('/room/insert', this.newRoom)
        .then(response => {
          // 添加成功后，关闭对话框，并刷新自习室列表
          this.addRoomDialogVisible = false;
          this.newRoom = { roomName: '' }; // 重置表单数据
          this.loadRooms(); // 重新加载自习室列表
          this.$message.success('添加自习室成功');
        })
        .catch(error => {
          console.error('添加自习室失败:', error);
          this.$message.error('添加自习室失败，请稍后重试');
        });
    },
    // 编辑自习室
    editRoom(room) {
      // 实现编辑自习室信息的逻辑
      console.log('编辑自习室信息', room);
      this.editingRoomIndex = this.roomData.indexOf(room);
      this.editRoomForm = { ...room };
      this.editRoomDialogVisible = true;
    },
    // 删除自习室信息
    deleteRoom(scope) {
      // 打开确认删除对话框
      this.deleteRoomDialogVisible = true;
      // 将要删除的自习室索引保存在变量中
      this.deletingRoomIndex = scope.row.roomId;
      console.log('删除自习室信息', scope.row.roomId);
    },


    // 保存编辑后的自习室信息
    saveEditedRoom() {
      axios.put('/room/update', {
        roomId: this.editRoomForm.roomId,
        roomName: this.editRoomForm.roomName

      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新自习室列表
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载自习室列表
            this.loadRooms();
            this.$message.success('成功修改自习室');
            this.editRoomDialogVisible = false;
          }
        })
        .catch(error => {
          console.error('修改自习室失败:', error);
          this.$message.error('修改自习室失败，请稍后重试');
        });
    },

    // 确认删除自习室
    confirmDeleteRoom() {
      // 关闭确认删除对话框
      this.deleteRoomDialogVisible = false;
      // 删除指定索引的用户

      axios.delete('/room/delete', {
        data: {
          roomId: this.deletingRoomIndex
        }
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新自习室列表
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载自习室列表
            this.loadRooms();
            this.$message.success('成功删除自习室');
          }
        })
        .catch(error => {
          console.error('删除自习室失败:', error);
          this.$message.error('删除自习室失败，请稍后重试');
        });
    },
    //------------------------------------------------------------
    // 座位信息加载
    loadSeats() {
      const url = 'seat/allseat';
      axios.get(url)
        .then(response => {
          // 处理获取到的座位数据
          const seats = response.data.map(seat => {
            const room = this.roomData.find(room => room.roomId === seat.roomId);
            if (room) {
              // 如果找到对应的房间信息，则返回包含座位号和对应的房间名的对象
              return {
                seatId: seat.seatId,
                seatNumber: seat.seatNumber,
                roomName: room.roomName,
                roomId: seat.roomId
              };
            } else {
              // 如果未找到对应的房间信息，则返回包含座位号和空字符串作为房间名的对象
              return {
                seatId: seat.seatId,
                seatNumber: seat.seatNumber,
                roomName: '',
                roomId: seat.roomId
              };
            }
          });
          // 将处理后的座位数据赋给 this.seatData
          this.seatData = seats;
          // 生成当天可视化
          this.buildSeatSchedulesForTodayAdmin();
        })
        .catch(error => {
          // 处理请求错误
          console.error('请求错误:', error);
          this.$message.error('网络错误，请稍后重试');
        });
    },
    // 编辑座位信息
    editSeat(seat) {
      // 实现编辑座位信息的逻辑
      console.log('编辑座位信息', seat);
      this.editingSeatIndex = this.seatData.indexOf(seat);
      this.editSeatForm = { ...seat };
      this.editSeatDialogVisible = true;
    },
    // 删除座位信息
    deleteSeat(scope) {
      // 打开确认删除对话框
      this.deleteSeatDialogVisible = true;
      // 将要删除的座位索引保存在变量中
      this.deletingSeatIndex = scope.row.seatId;
      console.log('删除座位信息', scope.row.seatId);
    },

    // 保存编辑后的座位信息
    saveEditedSeat() {
      // 检查是否存在相同的 seatNumber 和 roomId
      const isDuplicate = this.seatData.some(seat => {
        return seat.seatNumber.toString() === this.editSeatForm.seatNumber.toString()
          && seat.roomId.toString() === this.editSeatForm.roomId.toString();
      });

      if (isDuplicate) {
        this.$message.error('已存在相同的座位号和房间号，请更改');
        return; // 不发送请求
      }
      axios.put(`/seat/update`, this.editSeatForm)
        .then(response => {
          // 保存成功后更新座位列表数据
          this.seatData.splice(this.editingSeatIndex, 1, this.editSeatForm);
          this.loadSeats();
          this.editSeatDialogVisible = false;
          this.$message.success('保存成功');
        })
        .catch(error => {
          console.error('保存失败:', error);
          this.$message.error('保存失败，请稍后重试');
        });
    },

    // 确认删除座位
    confirmDeleteSeat() {
      // 关闭确认删除对话框
      this.deleteSeatDialogVisible = false
      axios.delete('/seat/delete', {
        data: {
          seatId: this.deletingSeatIndex
        }
      })
        .then(response => {
          // 删除成功后，重新加载自习室列表
          this.loadSeats();
          this.$message.success('成功删除座位');

        })
        .catch(error => {
          console.error('删除座位失败:', error);
          this.$message.error('删除座位失败，请稍后重试');
        });
    },
    // 显示添加座位对话框
    showAddSeatDialog() {
      this.addSeatDialogVisible = true;
    },
    // 取消添加座位操作
    cancelAddSeat() {
      this.addSeatDialogVisible = false;
      // 重置新座位的数据模型
      this.newSeat = {
        seatNumber: '',
        roomId: ''
      };
    },
    // 保存新添加的座位信息
    saveNewSeat() {
      // 检查是否存在相同的 seatNumber 和 roomId
      const isDuplicate = this.seatData.some(seat => {
        return seat.seatNumber.toString() === this.newSeat.seatNumber.toString() && seat.roomId.toString() === this.newSeat.roomId.toString();
      });
      if (isDuplicate) {
        this.$message.error('已存在相同的座位号和房间号，请更改');
        return; // 不发送请求
      }
      // 发送请求保存新添加的座位信息
      axios.post('/seat/insert', this.newSeat)
        .then(response => {
          // 添加成功后，关闭对话框，并刷新座位列表
          this.addSeatDialogVisible = false;
          this.newSeat = {
            seatNumber: '',
            roomId: ''
          }; // 重置新座位的数据模型
          this.loadSeats(); // 重新加载座位列表
          this.$message.success('添加座位成功');
        })
        .catch(error => {
          console.error('添加座位失败:', error);
          this.$message.error('添加座位失败，请稍后重试');
        });
    },
    //------------------------------------------------------------
    // 用户信息加载
    loadUsers(){
      const url = 'user/all';
      axios.get(url).then(response => {
        this.userData = response.data;
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      });
    },
    togglePassword(row) {//用户密码隐藏
      // 切换当前行的 showPassword 状态
      row.showPassword = !row.showPassword;
    },
    // 编辑用户信息
    editUser(user) {
      // 实现编辑用户信息的逻辑
      console.log('编辑用户信息', user);
      this.editingUserIndex = this.userData.indexOf(user);
      this.editUserForm = { ...user };
      this.editUserDialogVisible = true;
    },
    // 删除用户信息
    deleteUser(scope) {
      // 打开确认删除对话框
      this.deleteUserDialogVisible = true
      // 将要删除的用户索引保存在变量中
      this.deletingUserIndex = scope.row.userId;
      console.log('删除用户信息', scope.row.userId)
    },
    // 保存编辑后的用户信息
    saveEditedUser() {
      // 如果切换为未封禁，则清空封禁截至时间
      const payload = {
        userId: this.editUserForm.userId,
        userName: this.editUserForm.userName,
        userPassword: this.editUserForm.userPassword,
        userPrivilege: this.editUserForm.userPrivilege,
        userIllegalState: this.editUserForm.userIllegalState,
        userIllegalDate: this.editUserForm.userIllegalState === 0 ? null : this.editUserForm.userIllegalDate
      };

      axios.put('/user/update', payload)
        .then(response => {
          // 添加成功后，关闭对话框，并刷新自习室列表
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载自习室列表
            this.loadUsers()
            this.$message.success('成功修改用户');
            this.editUserDialogVisible = false;
          }
        })
        .catch(error => {
          console.error('修改用户失败:', error);
          this.$message.error('修改用户失败，请稍后重试');
        });
    },

    // 当封禁状态变为“未封禁”时，自动清除封禁截至时间
    onIllegalStateChange(val) {
      if (val === 0) {
        this.editUserForm.userIllegalDate = null;
      }
    },

    // 确认删除用户
    confirmDeleteUser() {
      // 关闭确认删除对话框
      this.deleteUserDialogVisible = false
      // 删除指定索引的预约信息
      axios.delete('/user/delete', {
        data: {
          userId : this.deletingUserIndex
        }
      })
        .then(response => {
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载用户列表
            this.loadUsers()
            this.$message.success('成功删除用户')
          }
        })
        .catch(error => {
          console.error('删除用户失败:', error)
          this.$message.error('删除用户失败')
        })
    },
    saveNewUser() {
      // 这里可以添加表单验证逻辑
      // 发送请求保存新添加的自习室信息
      axios.post('/user/register', {
        userAccount: this.newUser.userAccount,
        userPassword:this.newUser.userPassword ,
        userName: this.newUser.userName,
        userPrivilege:this.newUser.userPrivilege
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新用户列表
          this.addUserDialogVisible = false;
          this.newUser = {
            userAccount: '',
            userPassword: '',
            userName: '',
            userPrivilege:''
          }; // 重置表单数据
          this.loadUsers()// 重新加载用户列表
          this.$message.success('添加用户成功')
        })
        .catch(error => {
          console.error('添加用户失败:', error)
          this.$message.error('添加用户失败，请稍后重试')
        });
    },
    //展示添加用户界面
    showAddUserDialog() {
      this.addUserDialogVisible = true;
    },
    // 取消添加预约信息操作
    cancelAddUser() {
      this.addUserDialogVisible = false;
      this.newUser =
        {
          userAccount: '',
          userPassword: '',
          userName: ''
        }; // 重置表单数据
    },
    handleUserCurrentChange(val) {
      this.userCurrentPage = val
    },
    handleRoomCurrentChange(val) {
      this.roomCurrentPage = val;
    },
    handleSeatCurrentChange(val) {
      this.seatCurrentPage = val;
    },
    handleReserveCurrentChange(val) {
      this.reserveCurrentPage = val;
    },
    handleViolationCurrentChange(val) {
      this.violationCurrentPage = val;
    },
    //------------------------------------------------------------
    // 预约信息加载
    loadReserve() {
      const url = 'reserve/all';
      axios.get(url).then(response => {
        // 处理获取到的座位数据
        const reserve = response.data.map(reserve => {
          const room = this.roomData.find(room => room.roomId === reserve.reserveRoomId);
          if (room) {
            // 如果找到对应的房间信息，则返回包含座位号和对应的房间名的对象
            return {
                reserveId: reserve.reserveId,
                reserveUserAccount: reserve.reserveUserAccount,
                reserveRoomName: room.roomName,
                reserveSeatNumber: reserve.reserveSeatLabel ? reserve.reserveSeatLabel : reserve.reserveSeatNumber,
                reserveTimeBegin : reserve.reserveTimeBegin,
                reserveTimeEnd : reserve.reserveTimeEnd,
                timeSignIn : reserve.timeSignIn,
                timeSignOut : reserve.timeSignOut,
                // 供管理员端可视化计算使用
                reserveRoomId: reserve.reserveRoomId,
                reserveSeatIdRaw: reserve.reserveSeatNumber,
                reserveState: reserve.reserveState,
              };
          } else {
            // 如果未找到对应的房间信息，则返回包含座位号和空字符串作为房间名的对象
            return {
                reserveId: reserve.reserveId,
                reserveUserAccount: reserve.reserveUserAccount,
                reserveRoomName: ' ',
                reserveSeatNumber: reserve.reserveSeatLabel ? reserve.reserveSeatLabel : reserve.reserveSeatNumber,
                reserveTimeBegin : reserve.reserveTimeBegin,
                reserveTimeEnd : reserve.reserveTimeEnd,
                timeSignIn : reserve.timeSignIn,
                timeSignOut : reserve.timeSignOut,
                reserveRoomId: reserve.reserveRoomId,
                reserveSeatIdRaw: reserve.reserveSeatNumber,
                reserveState: reserve.reserveState,
              };
          }
        });
        this.reserveData = reserve;
        // 生成当天可视化
        this.buildSeatSchedulesForTodayAdmin();
      })
        .catch(error => {
          // 处理请求错误
          console.error('请求错误:', error);
          this.$message.error('网络错误，请稍后重试');
        });
    },
    // 构建管理员端：今日每个座位的连续条状态
    buildSeatSchedulesForTodayAdmin() {
      if (!this.scheduleDate || !this.seatData || this.seatData.length === 0 || !this.reserveData) return;
      const dayStart = new Date(`${this.scheduleDate} 08:00:00`);
      const dayEnd = new Date(`${this.scheduleDate} 22:30:00`);
      // 30分钟粒度
      const slots = [];
      for (let t = new Date(dayStart); t <= dayEnd; t = new Date(t.getTime() + 30 * 60 * 1000)) {
        slots.push({ start: new Date(t), end: new Date(t.getTime() + 30 * 60 * 1000), status: 'free' });
      }
      const segCount = slots.length;
      const segPercent = 100 / segCount;

      const reservedRanges = {};
      const freeRanges = {};

      // 为每个座位计算
      for (const seat of this.seatData) {
        const seatId = seat.seatId;
        const seatSlots = slots.map(s => ({ ...s }));
        const seatReservations = (this.reserveData || []).filter(r =>
          r.reserveState === 0 && Number(r.reserveSeatIdRaw) === Number(seatId) && Number(r.reserveRoomId) === Number(seat.roomId)
        );
        // 标记重叠为 reserved
        seatSlots.forEach(slot => {
          for (const r of seatReservations) {
            const rStart = new Date(r.reserveTimeBegin);
            const rEnd = new Date(r.reserveTimeEnd);
            if (slot.start < rEnd && slot.end > rStart) { slot.status = 'reserved'; break; }
          }
        });
        // 合并 reserved 段
        const ranges = [];
        let i = 0;
        while (i < segCount) {
          if (seatSlots[i].status === 'reserved') {
            const startIdx = i; while (i < segCount && seatSlots[i].status === 'reserved') i++; const endIdx = i - 1;
            const left = startIdx * segPercent; const width = (endIdx - startIdx + 1) * segPercent;
            ranges.push({ key: `${seatId}-${startIdx}-${endIdx}`, left, width });
          } else { i++; }
        }
        reservedRanges[seatId] = ranges;

        // 计算 free 连续段
        const freeList = [];
        let j = 0;
        const fmt = (d) => `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
        while (j < segCount) {
          if (seatSlots[j].status === 'free') {
            const startIdx = j; while (j < segCount && seatSlots[j].status === 'free') j++; const endIdx = j - 1;
            const left = startIdx * segPercent; const width = (endIdx - startIdx + 1) * segPercent;
            const begin = new Date(dayStart.getTime() + startIdx * 30 * 60 * 1000);
            const end = new Date(dayStart.getTime() + (endIdx + 1) * 30 * 60 * 1000);
            freeList.push({ key: `${seatId}-free-${startIdx}-${endIdx}`, left, width, begin, end, label: `${fmt(begin)}-${fmt(end)}` });
          } else { j++; }
        }
        freeRanges[seatId] = freeList;
      }
      this.seatReservedRanges = reservedRanges;
      this.seatFreeRanges = freeRanges;
    },
    onAdminBarHover(evt, seatId) {
      // 计算视口坐标并在 body 上展示固定定位 tooltip，避免裁剪
      const bar = evt.currentTarget;
      if (!bar) return;
      const rect = bar.getBoundingClientRect();
      const x = evt.clientX - rect.left;
      const percent = Math.max(0, Math.min(100, (x / rect.width) * 100));
      const ranges = this.seatFreeRanges[seatId] || [];
      const hit = ranges.find(r => percent >= r.left && percent <= (r.left + r.width));
      if (!hit) {
        // hide via rAF to avoid sync layout churn
        if (this.adminBarTooltipRaf) cancelAnimationFrame(this.adminBarTooltipRaf);
        this.adminBarTooltipRaf = requestAnimationFrame(() => {
          this.adminBarTooltipGlobal.visible = false;
          this.adminBarTooltipRaf = null;
        });
        return;
      }
      // 计算中心位置（视口坐标）
      const centerPercent = hit.left + hit.width / 2;
      const leftPx = rect.left + (centerPercent / 100) * rect.width;
      const topPx = rect.top - 36; // 在条上方显示
      // batch updates via requestAnimationFrame to avoid ResizeObserver loop
      if (this.adminBarTooltipRaf) cancelAnimationFrame(this.adminBarTooltipRaf);
      this.adminBarTooltipRaf = requestAnimationFrame(() => {
        this.adminBarTooltipGlobal.visible = true;
        this.adminBarTooltipGlobal.left = Math.round(leftPx);
        this.adminBarTooltipGlobal.top = Math.round(topPx);
        this.adminBarTooltipGlobal.label = hit.label;
        this.adminBarTooltipRaf = null;
      });
    },
    onAdminBarLeave() {
      if (this.adminBarTooltipRaf) cancelAnimationFrame(this.adminBarTooltipRaf);
      this.adminBarTooltipRaf = requestAnimationFrame(() => {
        this.adminBarTooltipGlobal.visible = false;
        this.adminBarTooltipRaf = null;
      });
    },
    // 日期选择器变更处理
    handleScheduleDateChange() {
      this.buildSeatSchedulesForTodayAdmin();
    },

    // 编辑预约信息
    editReserve(reserve) {
      // 实现编辑预约信息的逻辑
      console.log('编辑预约信息', reserve);
      this.editingReserveIndex = this.reserveData.indexOf(reserve);
      this.editReserveForm = { ...reserve };
      this.editReserveDialogVisible = true;
    },
    // 删除预约信息
    deleteReserve(scope) {
      // 打开确认删除对话框
      this.deleteReserveDialogVisible = true;
      this.deletingReserveIndex = scope.row.reserveId;
      console.log('删除预约信息', scope.row.reserveId)
    },
    //删除预约信息确认按钮
    confirmDeleteReserve() {
      // 关闭确认删除对话框
      this.deleteReserveDialogVisible = false
      // 删除指定索引的预约信息
      axios.delete('/reserve/delete', {
        data: {
          reserveId : this.deletingReserveIndex
        }
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新自习室列表
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载自习室列表
            this.loadReserve()
            this.$message.success('成功删除预约信息')
          }
        })
        .catch(error => {
          console.error('删除预约信息失败:', error)
          this.$message.error('删除预约信息失败，请稍后重试');
        })
    },
    // 保存编辑后的预约信息
    saveEditedReserve() {
      const room = this.roomData.find(room => room.roomName === this.editReserveForm.reserveRoomName );
      if(room)
      {
        axios.put('/reserve/update', {
          reserveId:this.editReserveForm.reserveId ,
          reserveUserAccount:this.editReserveForm.reserveUserAccount ,
          reserveRoomId: room.roomId,
          reserveSeatNumber:this.editReserveForm.reserveSeatNumber ,
          reserveTimeBegin : this.editReserveForm.reserveTimeBegin+':00',
          reserveTimeEnd :this.editReserveForm.reserveTimeEnd+':00'
        })
          .then(response => {
            // 添加成功后，关闭对话框，并刷新预约信息列表
            if (response.data.code === 200) {
              // 删除成功后，重新加载自习室列表
              this.loadReserve()
              this.$message.success('成功修改预约信息')
              this.editReserveDialogVisible = false
            }
            else if (response.data.code === 500)
            {
              this.$message.error('无法修改，座位被占用')
            }
          })
          .catch(error => {
            console.error('修改自习室失败:', error)
            this.$message.error('修改预约信息失败，请稍后重试')
          })
      }
      else {
        this.$message.error('无所填自习室')
      }
    },
    showAddReserveDialog() {
      // 确保打开对话框前数据是最新的
      this.loadRooms()
      this.loadSeats()
      this.addReserveDialogVisible = true
    },
    // 取消添加预约信息操作
    cancelAddReserve() {
      this.addReserveDialogVisible = false;
      this.newReserve =
        {
            reserveUserAccount: '',
            reserveSeatNumber: '',
            reserveRoomId: '',
            reserveTimeBegin: '',
            reserveTimeEnd: ''
        }; // 重置表单数据
    },
    // 保存新添加的预约信息
    saveNewReserve() {
      // 这里可以添加表单验证逻辑
      // 发送请求保存新添加的预约信息
      const url = `user/list?userAccount=${this.newReserve.reserveUserAccount}`
      axios.get(url).then(response => {
        const userData = response.data
        if(userData && userData.length > 0){
          // 客户端校验并规范化字段：确保座位号和房间号为数字，时间存在
          if (!this.newReserve.reserveRoomId) {
            this.$message.error('请选择自习室');
            return;
          }
          if (!this.newReserve.reserveSeatNumber) {
            this.$message.error('请输入座位号');
            return;
          }
          // 支持用户输入 A-104 或 104 等格式，提取数字部分作为座位号
          const seatStr = String(this.newReserve.reserveSeatNumber);
          const seatDigits = seatStr.match(/(\d+)$/);
          const seatNumberParsed = seatDigits ? parseInt(seatDigits[1], 10) : parseInt(seatStr.replace(/\D/g, ''), 10);
          if (Number.isNaN(seatNumberParsed)) {
            this.$message.error('座位号格式不正确，请输入例如 104 或 A-104');
            return;
          }
          if (!this.newReserve.reserveTimeBegin || !this.newReserve.reserveTimeEnd) {
            this.$message.error('请选择起止时间');
            return;
          }

          // 校验座位在所选房间中是否存在（更宽松的匹配：提取座位数字部分进行比较）
          const roomIdNum = Number(this.newReserve.reserveRoomId);
          console.debug('validate seat check', { roomIdNum, seatNumberParsed, seatDataLength: this.seatData.length });
          const seatExists = this.seatData.some(s => {
            try {
              const sRoomId = Number(s.roomId);
              const sSeatStr = String(s.seatNumber || '');
              const sDigits = sSeatStr.match(/(\d+)$/);
              const sNumber = sDigits ? parseInt(sDigits[1], 10) : parseInt(sSeatStr.replace(/\D/g, ''), 10);
              // 如果解析失败，sNumber 可能为 NaN
              if (Number.isNaN(sNumber)) return false;
              return sRoomId === roomIdNum && sNumber === seatNumberParsed;
            } catch (e) {
              return false;
            }
          });
          if (!seatExists) {
            console.warn('seat not found in selected room', { roomIdNum, seatNumberParsed, seatData: this.seatData.slice(0,20) });
            this.$message.error('所选自习室中未找到该座位，请确认座位号与自习室匹配');
            return;
          }

          const payload = {
            reserveUserAccount: this.newReserve.reserveUserAccount,
            reserveSeatNumber: seatNumberParsed,
            reserveRoomId: Number(this.newReserve.reserveRoomId),
            reserveTimeBegin: String(this.newReserve.reserveTimeBegin).endsWith(':00') ? this.newReserve.reserveTimeBegin : this.newReserve.reserveTimeBegin + ':00',
            reserveTimeEnd: String(this.newReserve.reserveTimeEnd).endsWith(':00') ? this.newReserve.reserveTimeEnd : this.newReserve.reserveTimeEnd + ':00'
          };
          // 在发送前打印完整 payload，便于后端调试
          console.debug('POST /reserve/add payload', payload);
          axios.post('/reserve/add', payload)
            .then(response => {
              // 后端使用 AjaxResult 返回 code 字段，HTTP 仍可能为 200
              if (response && response.data && response.data.code === 200) {
                // 添加成功
                this.addReserveDialogVisible = false;
                this.newReserve = {
                  reserveUserAccount: '',
                  reserveSeatNumber: '',
                  reserveRoomId: '',
                  reserveTimeBegin: '',
                  reserveTimeEnd: ''
                };
                this.loadReserve();
                this.$message.success('添加预约成功');
              } else {
                // 后端返回业务错误（code !== 200）
                const msg = (response && response.data && response.data.msg) ? response.data.msg : '添加预约失败';
                this.$message.error(msg);
              }
            })
            .catch(error => {
              console.error('添加预约失败:', error);
              if (error && error.response && error.response.data && error.response.data.msg) {
                this.$message.error(error.response.data.msg);
              } else if (error && error.response && error.response.statusText) {
                this.$message.error(error.response.statusText);
              } else {
                this.$message.error('添加预约失败，请稍后重试');
              }
            });
        } else {
          this.$message.error('未查询到该用户')
        }
      }).catch(error => {
        // 处理请求错误
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试')
      })
    },

    //----------------------------------------------
    // 加载违规信息数据
    loadViolations() {
      const url = 'log/all'; // 假设这是后端提供的获取违规信息的API
      axios.get(url).then(response => {
        this.violationData = response.data;
      }).catch(error => {
        console.error('请求错误:', error);
        this.$message.error('网络错误，请稍后重试');
      })
    },
    cancelAddViolation(){
      this.addViolationDialogVisible = false;
      this.newViolation=
        {
          userAccount: '',
          logState: ''
        }; // 重置表单数据
    },
    // 违规信息编辑
    editViolation(violation) {
      this.editingViolationIndex = this.violationData.indexOf(violation);
      this.editViolationForm = {...violation};
      this.editViolationDialogVisible = true;
      console.log('编辑违规信息', violation);
    },

    // 违规信息删除
    deleteViolation(scope) {
      // 打开确认删除对话框
      this.deleteViolationDialogVisible = true;
      // 将要删除的自习室索引保存在变量中
      this.deletingViolationIndex = scope.row.logId;
      console.log('删除违规信息', scope.row.logId)
    },
    // 确认删除违规信息
    confirmDeleteViolation() {
      // 关闭确认删除对话框
      this.deleteViolationDialogVisible = false
      // 删除指定索引的违规信息
      axios.delete('/log/delete', {
        data: {
          logId: this.deletingViolationIndex,
        }
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新违规信息列表
          if (response.data && response.data.code === 200) {
            // 删除成功后，重新加载违规信息列表
            this.loadViolations()
            this.$message.success('成功删除违规信息')
          }
        })
        .catch(error => {
          console.error('删除违规信息失败:', error)
          this.$message.error('删除违规信息失败，请稍后重试');
        })
    },
    // 保存编辑后的违规信息
    saveEditedViolation() {
      axios.put('/log/update', {
        logId:this.editViolationForm.logId,
        userAccount:this.editViolationForm.userAccount ,
        logState:this.editViolationForm.logState,
        logTime:this.editViolationForm.logTime+':00'
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新违规信息列表
          if (response.data.code === 200) {
            this.loadViolations()
            this.$message.success('成功修改违规信息')
            this.editViolationDialogVisible = false
          }
        })
        .catch(error => {
          console.error('修改违规信息失败:', error)
          this.$message.error('修改违规信息失败，请稍后重试')
        })
    },

    // 显示添加违规信息对话框
    showAddViolationDialog() {
        this.addViolationDialogVisible = true
    },

    // 保存新添加的违规信息
    saveNewViolation() {
      // 发送请求保存新添加的自习室信息
      axios.post('/log/insert', {
        userAccount: this.newViolation.userAccount,
        logState:this.newViolation.logState
      })
        .then(response => {
          // 添加成功后，关闭对话框，并刷新用户列表
          this.newUser = {
            userAccount: '',
            logState: ''
          }; // 重置表单数据
          this.loadViolations()
          this.$message.success('添加违规信息成功')
          this.addViolationDialogVisible = false
        })
        .catch(error => {
          console.error('添加违规信息失败:', error)
          this.$message.error('添加违规信息失败，请稍后重试')
        });
    },


    changelab() {
      const currentTime = new Date();
      const currentHour = currentTime.getHours();
      if (currentHour >= 8 && currentHour < 11) {
        return '早上好，管理员，开始您一天的工作吧！';
      } else if (currentHour >= 11 && currentHour < 13) {
        return '中午好，管理员，休息一下吧！';
      } else if (currentHour >= 13 && currentHour < 17) {
        return '下午好，管理员，继续努力！';
      } else if (currentHour >= 17 && currentHour < 23) {
        return '晚上好，管理员，您辛苦了！';
      } else {
        return '管理员，夜深了，请注意休息！';
      }
    },
    // 切换标签
    changeTab(tab) {
      this.currentTab = tab
    },
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

/* 编辑按钮样式 */
.edit-btn {
  border: none; /* 去除边框 */
  border-radius: 4px; /* 圆角 */
  padding: 10px 20px; /* 内边距 */
  cursor: pointer; /* 鼠标样式 */
  transition: background-color 0.3s ease; /* 过渡效果 */
  background-color: #409EFF;
  color: #fff;
}

/* 删除按钮样式 */
.delete-btn {
  border: none; /* 去除边框 */
  border-radius: 4px; /* 圆角 */
  padding: 10px 20px; /* 内边距 */
  cursor: pointer; /* 鼠标样式 */
  transition: background-color 0.3s ease; /* 过渡效果 */
  background-color: #F56C6C;
  color: #fff;
}

/* 取消按钮样式 */
.cancel-btn {
  border: none; /* 去除边框 */
  border-radius: 4px; /* 圆角 */
  padding: 10px 20px; /* 内边距 */
  cursor: pointer; /* 鼠标样式 */
  transition: background-color 0.3s ease; /* 过渡效果 */
  background-color: #909399;
  color: #fff;
}

/* 保存按钮样式 */
.save-btn {
  border: none; /* 去除边框 */
  border-radius: 4px; /* 圆角 */
  padding: 10px 20px; /* 内边距 */
  cursor: pointer; /* 鼠标样式 */
  transition: background-color 0.3s ease; /* 过渡效果 */
  background-color: #67C23A;
  color: #fff;
}

.add-btn {
  background-color: #409EFF; /* 背景颜色 */
  color: #FFFFFF; /* 文字颜色 */
  border: none; /* 去除边框 */
  border-radius: 4px; /* 圆角 */
  padding: 10px 20px; /* 内边距 */
  cursor: pointer; /* 鼠标样式 */
  transition: background-color 0.3s ease; /* 过渡效果 */

  /* 鼠标悬停样式 */

  &:hover {
    background-color: #66B1FF;
  }

  /* 按下样式 */

  &:active {
    background-color: #1F90FF;
  }
}

.cell {
  display: flex;
}

/* 表格内操作按钮布局，保证编辑/删除对齐 */
.table-ops {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  align-items: center;
}

.el-table .edit-btn,
.el-table .delete-btn {
  padding: 6px 10px; /* 表格内按钮更小的内边距，避免高度不一致 */
}

/* 按钮图标样式 */
.el-icon {
  font-size: 16px;
}

.el-main {
  background-color: #e9eef3;
  color: #334a97;
  padding: 20px;
}

.el-header {
  background-color: #334a97;
  color: #ffffff;
  line-height: 60px;
  padding-left: 0;
}

.logo {
  font-weight: bold;
  line-height: 50px; /* 保证文字与图标在同一水平线上 */
  margin-top: 5px;
}

.logo-image {
  max-width: 50px; /* 设置图标的最大宽度 */
  max-height: 50px; /* 设置图标的最大高度 */
  float: left;
  margin-right: 10px; /* 图标右边距 */
  margin-left: 5px;
  margin-top: 5px;
}

.card-container {
  display: flex;
  height: 80px;
}

.ava {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.admin-info h3 {
  margin-left: 24px;
  padding-top: 12px;
  margin-bottom: 12px;
}

.weather {
  margin-left: 24px;
}

.homeimage {
  width: 100%;
  height: 100%;
}

.header {
  display: flex;
  background-color: #3e5ebd; /* 设置背景颜色 */
  color: #fff; /* 设置字体颜色 */
}

/* 座位预约状态条（管理员端表格内） */
.schedule-bar-wrapper {
  position: relative;
  width: 100%;
  overflow: visible !important;
}
.schedule-bar {
  position: relative;
  height: 16px;
  width: 100%;
  background-color: #4CAF50; /* 可预约-绿色底条 */
  border-radius: 8px;
  overflow: visible !important;
}
.schedule-bar.in-table { height: 12px; }
.bar-reserved {
  position: absolute;
  top: 0;
  height: 100%;
  background-color: #E53935; /* 已预约-红色覆盖 */
  border-radius: 8px;
}
.bar-tooltip-overlay {
  position: absolute;
  top: -32px;
  transform: translateX(-50%);
  background: #ffffff;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 3px 12px rgba(0,0,0,0.2);
  z-index: 99999 !important;
  pointer-events: none;
}

/* 强制表格允许溢出 */
:deep(.el-table__body-wrapper) {
  overflow: visible !important;
}
:deep(.el-table__body) {
  overflow: visible !important;
}
:deep(.el-table td.el-table__cell) {
  overflow: visible !important;
}
:deep(.el-table th.el-table__cell) {
  overflow: visible !important;
}
:deep(.el-table__row) {
  overflow: visible !important;
}

/* 全局悬浮 tooltip（fixed） */
.admin-floating-tooltip {
  position: fixed !important;
  transform: translateX(-50%);
  z-index: 2147483647 !important;
  background: #fff;
  color: #222;
  border: 1px solid rgba(0,0,0,0.08);
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.25);
  pointer-events: none;
}
</style>
