<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission" />
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="500px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="审批编码">
            <el-input v-model="form.approvalId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="模块编码" prop="moduleId">
            <el-input v-model="form.moduleId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="申请人编码">
            <el-input v-model="form.applicantId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="申请人名字">
            <el-input v-model="form.applicantName" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批人编码">
            <el-input v-model="form.approverId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批人名字">
            <el-input v-model="form.approverName" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批流程编码">
            <el-input v-model="form.processId" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="等级">
            <el-input v-model="form.levels" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批驳回原因">
            <el-input v-model="form.rejectReason" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="创建时间">
            <el-input v-model="form.createTime" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="修改时间">
            <el-input v-model="form.modifyTime" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批完成时间">
            <el-input v-model="form.completeTime" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="审批状态 0审批中 1审批通过 2审批驳回">
            <el-input v-model="form.status" style="width: 370px;" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="approvalId" label="审批编码" />
        <el-table-column prop="moduleId" label="模块编码" />
        <el-table-column prop="applicantId" label="申请人编码" />
        <el-table-column prop="applicantName" label="申请人名字" />
        <el-table-column prop="approverId" label="审批人编码" />
        <el-table-column prop="approverName" label="审批人名字" />
        <el-table-column prop="processId" label="审批流程编码" />
        <el-table-column prop="levels" label="等级" />
        <el-table-column prop="rejectReason" label="审批驳回原因" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="modifyTime" label="修改时间" />
        <el-table-column prop="completeTime" label="审批完成时间" />
        <el-table-column prop="status" label="审批状态 0审批中 1审批通过 2审批驳回" />
        <el-table-column v-if="checkPer(['admin','approval:edit','approval:del'])" label="操作" width="150px" align="center">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination />
    </div>
  </div>
</template>

<script>
import crudApproval from '@/api/approval'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { approvalId: null, moduleId: null, applicantId: null, applicantName: null, approverId: null, approverName: null, processId: null, levels: null, rejectReason: null, createTime: null, modifyTime: null, completeTime: null, status: null }
export default {
  name: 'Approval',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  cruds() {
    return CRUD({ title: 'ApprovalController', url: 'api/approval', idField: 'approvalId', sort: 'approvalId,desc', crudMethod: { ...crudApproval }})
  },
  data() {
    return {
      permission: {
        add: ['admin', 'approval:add'],
        edit: ['admin', 'approval:edit'],
        del: ['admin', 'approval:del']
      },
      rules: {
        moduleId: [
          { required: true, message: '模块编码不能为空', trigger: 'blur' }
        ]
      }    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    }
  }
}
</script>

<style scoped>

</style>
