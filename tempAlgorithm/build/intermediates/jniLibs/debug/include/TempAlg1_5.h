/*
version:BA_1.5_3.2.1 ----2019/10/14 //对外算法状态统一10改为6，不再区分公司和润生算法
version:BA_1.5_3.2.0 ----2019/9/24 //对外算法状态10改为6；ProoResult新增字段predCheck,App暂时不使用
version:BA_1.5_3.1.9 ----2019/8/14 //新增getVersion接口，返回当前算法版本信息号；
version:BA_1.5_3.1.8 ----2019/7/17 //降温策略修正，增加状态5->3的跳转，区分润生算法需要检测重新开始，我们的不用；
version:BA_1.5_3.1.7 ----2019/7/16 //降温策略修改：5min每分钟降0.05改为0.03，如果前一算法温度高于40度仍采用0.05；
version:BA_1.5_3.1.6 ----2019/7/10 //降温策略统一阈值为3度，升温要求3min出预测温度，二次升温预测不得比前一算法温度低0.2度以上，修正stabInit重复调用bug，贴上到预测过渡段新增处理
*/
#ifndef TEMPALG_H
#define TEMPALG_H

#include <stdio.h>
#include <stdint.h>



struct ProResult {
	float stabTemp;//算法处理后温度结果
	int status;//测量状态：0首次升温前的取下,1贴上未夹紧,2首次升温,3稳定阶段,4二次升温,10首次升温过程后的取下
	int samp;//算法需要数据的采样率
	int gesture;//手臂姿势状态,0--张开到夹紧，1--夹紧，2--夹紧到张开，3--张开, 4--脱落
	bool predCheck;//输出是否成功完成3min预测or二次预测
};


/**
*APP接口
*temp：实时温度 (float类型)
*time:每个温度点对应得时间戳，单位ms
*flag:测量中的设备标志，不同设备标志是唯一的
*state:测量状态,0测量开始（算法内部进行空间分配），1测量中，2测量结束（算法内部进行空间释放）
*isPill:吃药情况，暂不做处理，可传入0
*samp:蓝牙连接的情况下为采样率，广播连接的情况下为包序
*connectType:连接方式，0--蓝牙，1--广播
*/
ProResult appHandle(float temp, long long time, int16_t flag, int16_t state, int16_t isPill, int16_t samp, int16_t connectType);

char* getVersion();

#endif // !TEMPALG_H

