#pragma once

#include <vector>

using namespace std;

struct TempImg {
	//int takeDown;
	bool handle;
	int startInd;
	int stickN;
	int decline;
	int predInd;
	int firstStart;//新增
	int status;//新增，状态值，0--取下，1--贴上未夹紧，2--首次升温，3--稳定阶段，1.0没有二次升温
	float stabT;//算法返回的温度值
	float predTemp;
};

/**
 * 预测成功后的曲线拟合
 * @param data 算法处理的温度值
 * @param pred 预测值
 * @param startInd 算法处理后，返回结构体中的firstStart
 * @return 返回拟合后的曲线
 */
vector<float> riseCurve(vector<float> data, float pred, int startInd);

/***
 * 1.0体温实时处理算法
 * @param data 实时采集的温度结合
 * @param preVal 上一次算法处理返回的结构，为返回结构体中的stabT
 * @param predTemp 算法中的预测值，为返回结构中的predTemp
 * @param startInd 算法预测的开始点，为返回结构体中的startInd
 * @param predInd 算法预测的结束点，为返回结构体中的startInd
 * @param stickN 算法判断的贴上点，为返回结构体中的stickN
 * @param decline 算法判断的降温点，为返回结构体中的decline
 * @param handle 为返回结构体中的handle
 * @param firstStart 为返回结构体中的firstStart
 * @return 返回状态信息，温度信息以及需要再次传入的信息
 */
TempImg
getTemp(vector<float> data, float preVal, float predTemp, int startInd, int predInd, int stickN,
		int decline, bool handle, int firstStart);
