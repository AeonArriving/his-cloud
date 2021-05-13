package com.lwy.his.controller;



import com.github.pagehelper.PageHelper;
import com.lwy.his.entity.assets.*;
import com.lwy.his.entity.doctor.DocotrInspectionrecord;
import com.lwy.his.entity.doctor.DoctorDrugrecord;
import com.lwy.his.entity.doctor.DoctorNodrugrecord;
import com.lwy.his.entity.doctor.DoctorTestrecode;
import com.lwy.his.entity.temporary.Allitems;
import com.lwy.his.entity.temporary.TableDateDrug;
import com.lwy.his.entity.temporary.TableDateNoDrug;
import com.lwy.his.service.DoctorHomeService;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/DoctorHomeController")
@Api("医生登陆主界面，操控各种各样的患者")
@Slf4j
public class DoctorHomeController {

    @Autowired
    private DoctorHomeService service;


    @RequestMapping("/selectallPRrecode")
    @ApiOperation("未就诊/就诊 根据患者名字，医生名字，日期查询")
    CopyOnWriteArrayList<Patient_Registration_record> selectallPRrecode(@RequestParam("patientusername") String patientusername,
                                                                        @RequestParam("today") String today,
                                                                        @RequestParam("doctorusername") String doctorusername,
                                                                        @RequestParam("prralready") int prralready,
                                                                        @RequestParam(value = "pageNo",defaultValue = "1") int pageNo,
                                                                        @RequestParam(value = "pageSize",defaultValue = "4") int pageSize) throws ParseException {
        Date today1 = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        if(today == null || today.length()==0 || today == ""){
            today = simpleDateFormat.format(today1);
        }
        //获取传递进来的下一天
        String s1 = today.substring(0, 8);
        String tomorrow = s1 + Integer.toString(Integer.parseInt(today.substring(8)) + 1);
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("today",today);
        map.put("tomorrow",tomorrow);
        map.put("patientusername",patientusername);
        map.put("doctorusername",doctorusername);
        //判断就诊否
        map.put("prralready",prralready);
        PageHelper.startPage(pageNo,pageSize);
        return service.selectallPRrecode(map);
    }
    @RequestMapping("/selectallPRrecodecount")
    @ApiOperation("未就诊/就诊 根据患者名字，医生名字，日期查询  查询总条数")
    int selectallPRrecodecount(@RequestParam("patientusername") String patientusername,
                               @RequestParam("today") String today,
                               @RequestParam("doctorusername") String doctorusername,
                               @RequestParam("prralready") int prralready) throws ParseException {
        Date today1 = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        if(today == null || today.length()==0 || today == ""){
            today = simpleDateFormat.format(today1);
        }
        //获取传递进来的下一天
        String s1 = today.substring(0, 8);
        String tomorrow = s1 + Integer.toString(Integer.parseInt(today.substring(8)) + 1);
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("today",today);
        map.put("tomorrow",tomorrow);
        map.put("patientusername",patientusername);
        map.put("doctorusername",doctorusername);
        //判断就诊否
        map.put("prralready",prralready);
        int size = service.selectallPRrecode(map).size();

        return size;

    }

    @RequestMapping("/selectPRRandMRbyid")
    @ApiOperation("根据挂号id查找挂号信息，病例信息")
    public Patient_Registration_record selectPRRandMRbyid(@RequestParam("id") int id) {
        return service.selectPRRandMRbyid(id);
    }

    @RequestMapping("/updatePrralready")
    @ApiOperation("根据挂号id更改已经就诊")
    public void updatePrralready(@RequestParam("prrid")  int prrid) {
        service.updatePrralready(prrid);
    }
    @RequestMapping("/updatePRRinf")
    @ApiOperation("根据挂号id修改病历里相关信息，主诉等信息")
    public void updatePRRinf(@RequestParam("prrid") int prrid,
                             @RequestParam("mrchiefcomplaint") String mrchiefcomplaint,
                             @RequestParam("mrpresentIllness") String mrpresentIllness,
                             @RequestParam("mrpast") String mrpast,
                             @RequestParam("mrpersonal") String mrpersonal,
                             @RequestParam("mrfinal") String mrfinal) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        map.put("prrid",prrid);
        map.put("mrchiefcomplaint",mrchiefcomplaint);
        map.put("mrpresentIllness",mrpresentIllness);
        map.put("mrpast",mrpast);
        map.put("mrpersonal",mrpersonal);
        map.put("mrfinal",mrfinal);

        service.updatePRRinf(map);
    }

    @RequestMapping("/selectallDiagnosis")
    @ApiOperation("查找所有的疾病信息")
    public CopyOnWriteArrayList<Diagnosis> selectallDiagnosis() {
        return service.selectallDiagnosis();
    }


    @RequestMapping("/selectallInspection")
    @ApiOperation("查找所有的检验信息")
    public CopyOnWriteArrayList<Inspection_items> selectallInspection() {
        return service.selectallInspection();
    }

    @RequestMapping("/selectalltest")
    @ApiOperation("查找所有的检查信息")
    public CopyOnWriteArrayList<Test_items> selectalltest() {
        return service.selectalltest();
    }

    @RequestMapping("/upAllitems")
    @ApiOperation("提交检查检验结果，到检查表，检验表，和消费记录")
    @GlobalTransactional(name = "his-order",rollbackFor = Exception.class)
    public void upAllitems(@RequestBody Allitems[] thisAllitems){

            String id = "";

            for (int i=0;i<thisAllitems.length;i++){
                //根据挂号id查找病历id
                int mrid = service.selectMRid(thisAllitems[i].getPrrid());
                //根据id种类不同 分类处理
                // 存入检查表
                 if(thisAllitems[i].getId().startsWith("i")){
                     Inspection inspection = new Inspection();
                     //查找检验表有多少条数据 手动写入自增id
                     int countInspection = service.countInspection();
                     id = "in"+Integer.toString(countInspection*2);
                     log.info(id);
                     inspection.setInid(id);
                     inspection.setInmrid(mrid);
                     inspection.setIndo(thisAllitems[i].getId());
                     inspection.setInend("未做检查无结果");
                     inspection.setInnum(1);
                     inspection.setIntime(new Timestamp(new Date().getTime()));
                     inspection.setInalive(0);
                     inspection.setIngivemoney(0);
                     inspection.setInused(0);

                     //统计检查医生表中有几条数据
                     int countDoctorInspectionrecord = service.countDoctorInspectionrecord();
                     //提交到检查医生表
                     DocotrInspectionrecord docotrInspectionrecord = new DocotrInspectionrecord();
                     log.info("dir"+Integer.toString(countDoctorInspectionrecord*2));
                     docotrInspectionrecord.setDirid("dir"+Integer.toString(countDoctorInspectionrecord*2));
                     docotrInspectionrecord.setDirmrid(mrid);
                     docotrInspectionrecord.setDiriid(thisAllitems[i].getId());
                     service.insertDoctorInspectionrecord(docotrInspectionrecord);

                     inspection.setInwater(docotrInspectionrecord.getDirid());
                     //提交到检查表
                     service.insertInspection(inspection);


                 }
                 //存入检验表
                 else{
                     Test test = new Test();
                     //查找检验表有多少条数据 手动写入自增id
                     int countTest = service.countTest();
                     id = "te"+Integer.toString(countTest*2);
                     log.info(id);
                     test.setTid(id);
                     test.setTmrid(mrid);
                     test.setTdo(thisAllitems[i].getId());
                     test.setTend("未做检查无结果");
                     test.setTnum(1);
                     test.setTtime(new Timestamp(new Date().getTime()));
                     test.setTalive(0);
                     test.setTgivemoney(0);
                     test.setTused(0);

                     //统计检验医生表中有几条数据
                     int countDoctorTestrecord = service.countDoctorTestrecord();
                     //提交到检验医生表  操作时间，操作医生为空
                     DoctorTestrecode doctorTestrecode = new DoctorTestrecode();
                    log.info("dtr"+Integer.toString(countDoctorTestrecord*2));
                     doctorTestrecode.setDtrid("dtr"+Integer.toString(countDoctorTestrecord*2));
                     doctorTestrecode.setDtrmrid(mrid);
                     doctorTestrecode.setDtrtid(thisAllitems[i].getId());
                     service.insertDoctorTestrecord(doctorTestrecode);

                     test.setTwater(doctorTestrecode.getDtrid());
                     //提交到检验表
                     service.insertTest(test);
                 }
                 //存入缴费表
                 Pay pay = new Pay();
                 pay.setPmrid(mrid);
                 pay.setProid(id);
                 pay.setPmoney( thisAllitems[i].getMoney());
                 pay.setPnum(1);
                 pay.setPallmoney(thisAllitems[i].getMoney());
                 pay.setPtime(new Timestamp(new Date().getTime()));
                 pay.setPtype("未选择");
                 pay.setPgivemoney(0);
                 pay.setPalive(0);
                 //提交到缴费表
                service.insertPay(pay);
                //记录Pay 和 Handle对应的信息
                DrugOrHandleWithPayRecord drugOrHandleWithPayRecord = new  DrugOrHandleWithPayRecord();
                drugOrHandleWithPayRecord.setDhid(id);
                drugOrHandleWithPayRecord.setPid(Integer.toString(service.selectpayid()));
                //提交到Pay -Drug-Handle 表
                service.insertDrugOrHandleWithPayRecord(drugOrHandleWithPayRecord);
            }
    }

    @RequestMapping("/selectallTestAndInsprction")
    @ApiOperation("根据病历id查找 检查，检验，缴费得信息")
    public CopyOnWriteArrayList<Medical_record> selectallTestAndInsprction(@RequestParam("prrid") int prrid) {
        //根据 prrid 找到 mrid
        int mrid = service.selectMRid(prrid);
        //根据mrid 查找相关病历信息
        CopyOnWriteArrayList<Medical_record> list = service.selectallTestAndInsprction(mrid);
        //System.out.println(list.toString());
        return list;

    }

    @RequestMapping("/updateTestInspectionPay")
    @ApiOperation("根据病历id 去 病历支付表找到对应的pay表id 再 把相对应alive设置为1  执行退回功能")
    public void updateTestInspectionPay(@RequestParam("mrid") int mrid,
                                        @RequestParam("id") String id,
                                        @RequestParam("time") String time) throws ParseException {
        ConcurrentMap map = new ConcurrentHashMap();

        //根据id 类型，是检查in 还是检验 t 来分类操作
        log.info("this is test or in id :"+id);
        if(id.substring(0,1).equals("t")){

               map.put("mrid",mrid);
               map.put("id",id);
               service.updateTestalive(map);
        }
        else {
                map.put("mrid",mrid);
                map.put("id",id);
                service.updateInspectionalive(map);
        }
        //pay
        //去 drughandlewithpayrecord 里找到 对应的pay id 再更改相应的 pay 的
        service.updatepayalive(map);



    }

    @RequestMapping("/selectallDrug")
    @ApiOperation("返回所有药品目录")
    public CopyOnWriteArrayList<Drug> selectallDrug() {
        return service.selectallDrug();
    }

    @RequestMapping("/selectallNoDrug")
    @ApiOperation("返回所有非药品目录")
    public CopyOnWriteArrayList<NoDrug> selectallNoDrug() {
        return service.selectallNoDrug();
    }

    @RequestMapping("/insertpayfromDrug")
    @ApiOperation("添加药品到支付列表")
    @GlobalTransactional(name = "his-order",rollbackFor = Exception.class)
    public void insertpayfromDrug(@RequestBody TableDateDrug[] tableDataDrug) {
//        (drid=dr18, drname=整肠生, drformat=盒, drconsumption=一日四次, drtype=无,
//        drnum=5555, dryounum=15, drmoney=15.5, drmedical=1, allmoney=232.5, prrid=4)

        for (TableDateDrug d : tableDataDrug){
            //根据prrid 找到Mrid
            int mrid = service.selectMRid(d.getPrrid());


            //修改drug 数量
            ConcurrentMap map = new ConcurrentHashMap();
            map.put("drid",d.getDrid());
            map.put("num",d.getDryounum());
            service.updateDrugNum(map);

            //提交到DoctorDrugrecord 药品医生记录表
            //统计药品医生记录表数量
            int countDoctorDrugrecord = service.countDoctorDrugrecord();
            //添加到开药流水表里
            DoctorDrugrecord doctorDrugrecord = new DoctorDrugrecord();
            doctorDrugrecord.setDdrid("ddr"+Integer.toString(countDoctorDrugrecord+1));
            doctorDrugrecord.setDdrmrid(mrid);
            doctorDrugrecord.setDdrdid(d.getDrid());
            //提交
             service.insertDoctorDrugrecord(doctorDrugrecord);

            //提交到Handle表
            Handle handle = new Handle();
            //获取Handle表条数
            int countHandle = service.countHandle();
            handle.setHid("h"+Integer.toString(countHandle+1));
            handle.setHmrid(mrid);
            handle.setHdo(d.getDrid());
            handle.setHnum(d.getDryounum());
            handle.setHtime(new Timestamp(new Date().getTime()));
            handle.setHalive(0);
            handle.setHgivemoney(0);
            handle.setHused(0);
            handle.setHwater(doctorDrugrecord.getDdrid());

            //提交到pay表
            Pay pay = new Pay();
            pay.setPmrid(mrid);
            pay.setProid(handle.getHid());
            pay.setPmoney(d.getDrmoney());
            pay.setPnum(d.getDryounum());
            pay.setPallmoney(d.getAllmoney());
            pay.setPtime(new Timestamp(new Date().getTime()));
            pay.setPtype("未选择");
            pay.setPgivemoney(0);
            pay.setPalive(0);
            //提交到pay表
            service.insertPayfromdrug(pay);
            //提交
            service.insertHandle(handle);
            //记录Pay 和 Handle对应的信息
            DrugOrHandleWithPayRecord drugOrHandleWithPayRecord = new  DrugOrHandleWithPayRecord();
            drugOrHandleWithPayRecord.setDhid(handle.getHid());
            drugOrHandleWithPayRecord.setPid(Integer.toString(service.countPay()+62));
            //提交到Pay -Drug-Handle 表
            service.insertDrugOrHandleWithPayRecord(drugOrHandleWithPayRecord);
        }
    }

    @RequestMapping("/uploadallNoDrug")
    @ApiOperation("提交非药品医疗处理")
    @GlobalTransactional(name = "his-order",rollbackFor = Exception.class)
    public void uploadallNoDrug(@RequestBody TableDateNoDrug[] tableDateNoDrugs){
//        TableDateNoDrug(nid=ndr10, nname=产后护理, pinyin=null, nformat=次, nnomney=null, nmediacl=0, nnum=12, nallmoney=1200000, prrid=2)
//        TableDateNoDrug(nid=ndr1, nname=静脉注射葡萄糖, pinyin=null, nformat=次, nnomney=null, nmediacl=0, nnum=11, nallmoney=440, prrid=2)
        for(TableDateNoDrug t : tableDateNoDrugs){
            //System.out.println(t);
            //根据prrid 找到Mrid
            int mrid = service.selectMRid(t.getPrrid());

            //提交到非药品处理流水表中
            //统计非药品流水记录表的数量
            int countDoctorNoDrugRecord = service.countDoctorNoDrugRecord();
            String dndrid = "dndr"+Integer.toString(countDoctorNoDrugRecord+1);
           // System.out.println("****************"+dndrid);
            DoctorNodrugrecord doctorNodrugrecord = new DoctorNodrugrecord();
            doctorNodrugrecord.setDndrid(dndrid);
            doctorNodrugrecord.setDndrmrid(mrid);
            //根据病历表查找医生id
            int drid = service.selectDridByMR(mrid);
           // doctorNodrugrecord.setDndrduid(drid);
            doctorNodrugrecord.setDndrndid(t.getNid());
            //doctorNodrugrecord.setDndrtime(new Timestamp(new Date().getTime()));
            //提交到非药品医生记录表n
            service.insertDoctorNoDrugRedord(doctorNodrugrecord);
            //提交到Handle表
            Handle handle = new Handle();
            //获取Handle表条数
            int countHandle = service.countHandle();
            handle.setHid("h"+Integer.toString(countHandle+1));
            handle.setHmrid(mrid);
            handle.setHdo(t.getNid());
            handle.setHnum(t.getNnum());
            handle.setHtime(new Timestamp(new Date().getTime()));
            handle.setHalive(0);
            handle.setHgivemoney(0);
            handle.setHused(0);
            handle.setHwater(doctorNodrugrecord.getDndrid());
            //提交
            service.insertHandle(handle);
            //提交到pay表
            Pay pay = new Pay();
            pay.setPmrid(mrid);
            pay.setProid(handle.getHid());
            pay.setPmoney(t.getNmoney());
            pay.setPnum(t.getNnum());
            pay.setPallmoney(t.getNallmoney());
            pay.setPtime(new Timestamp(new Date().getTime()));
            pay.setPtype("未选择");
            pay.setPgivemoney(0);
            pay.setPalive(0);
            //提交到pay表
            service.insertPayfromdrug(pay);
            //记录Pay 和 Handle对应的信息
            DrugOrHandleWithPayRecord drugOrHandleWithPayRecord = new  DrugOrHandleWithPayRecord();
            drugOrHandleWithPayRecord.setDhid(handle.getHid());
            drugOrHandleWithPayRecord.setPid(Integer.toString(service.selectpayid()));
            //提交到Pay -Drug-Handle 表
            service.insertDrugOrHandleWithPayRecord(drugOrHandleWithPayRecord);
        }
    }
    @ApiOperation("根据prrid 返回所有药品所作处理的所有信息 nodrug")
    @RequestMapping("/selectallHandle")
    public CopyOnWriteArrayList<Handle> selectallHandle(@RequestParam("prrid") Integer prrid) {
        //System.out.println(prrid);
        //根据prrid 查找mrid
        int mrid = service.selectMRid(prrid);
        CopyOnWriteArrayList<Handle> handles = service.selectallHandle(mrid);
        handles.forEach((i)->{
            //循环 把id 替换成名字
            String hdo = i.getHdo();
            //非药品
            if(hdo.startsWith("n")){
                //根据id 在非药品清单里查找
                String name = service.selectNameByidFromNoDrug(hdo);
                i.setHdo(name);
            }
            //药品
            else{
                //根据id 在药品清单里查找
                String name = service.selectNameByidFromDrug(hdo);
                i.setHdo(name);
            }

            log.info(String.valueOf(i));
        });
        return handles;
    }

    @ApiOperation("根据hid 退回药品及非药品 （handle）")
    @RequestMapping("/deleteHandle")
    public void deleteHandle(@RequestParam("hid") String hid) {
           //对handle表 进行 退回
        service.deleteHandle(hid);
        //对 pay表进行退回
         service.deletepaybyhandle(hid);
    }





}
