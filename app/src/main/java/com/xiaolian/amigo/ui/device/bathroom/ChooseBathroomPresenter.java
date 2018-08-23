package com.xiaolian.amigo.ui.device.bathroom;

import android.util.Log;

import com.xiaolian.amigo.data.enumeration.BathTradeType;
import com.xiaolian.amigo.data.manager.intf.IBathroomDataManager;
import com.xiaolian.amigo.data.network.model.ApiResult;
import com.xiaolian.amigo.data.network.model.bathroom.BathBookingReqDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathBookingRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathBookingStatusReqDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathBuildingRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathOrderCurrentRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BathOrderPreconditionRespDTO;
import com.xiaolian.amigo.data.network.model.bathroom.BuildingTrafficDTO;
import com.xiaolian.amigo.data.network.model.bathroom.TryBookingResultRespDTO;
import com.xiaolian.amigo.data.network.model.common.SimpleReqDTO;
import com.xiaolian.amigo.ui.base.BasePresenter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomPresenter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomView;
import com.xiaolian.amigo.util.RxHelper;

import javax.inject.Inject;

import rx.functions.Action1;

import static com.xiaolian.amigo.util.Constant.ACCEPTED;
import static com.xiaolian.amigo.util.Constant.BOOKING_SUCCESS;
import static com.xiaolian.amigo.util.Constant.FAIL;
import static com.xiaolian.amigo.util.Constant.INIT;
import static com.xiaolian.amigo.util.Constant.OPENED;
import static com.xiaolian.amigo.util.Constant.ORDER_SETTLE;
import static com.xiaolian.amigo.util.Constant.ORDER_USING;
import static com.xiaolian.amigo.util.Constant.PRE_USING;
import static com.xiaolian.amigo.util.Constant.QUEUEING;

/**
 * @author zcd
 * @date 18/7/3
 */
public class ChooseBathroomPresenter<V extends IChooseBathroomView> extends BasePresenter<V>
        implements IChooseBathroomPresenter<V> {
    private static final String TAG = ChooseBathroomPresenter.class.getSimpleName();
    private IBathroomDataManager bathroomDataManager;

    private static int time = 0  ;

    private boolean isResume = false ;

    public  boolean isReferBathroom = true ;

    @Inject
    public ChooseBathroomPresenter(IBathroomDataManager bathroomDataManager) {
        this.bathroomDataManager = bathroomDataManager;
    }



    @Override
    public void getBathroomList(long buildingId ) {
        SimpleReqDTO reqDTO = new SimpleReqDTO();
        reqDTO.setId(buildingId);
        addObserver(bathroomDataManager.tree(reqDTO),
                new NetworkObserver<ApiResult<BathBuildingRespDTO>>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onReady(ApiResult<BathBuildingRespDTO> result) {
                        if (null == result.getError()) {
                            if (isResume) {
                                getMvpView().setTvTitle(result.getData().getBuildingName());
                                Log.e(TAG, "onReady: " + isReferBathroom );
                                if (isReferBathroom) {
                                    getMvpView().refreshBathroom(result.getData());
                                }
                                delay(60, new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {

                                        getBathroomList(buildingId);
                                    }
                                });
                            }
                        } else {
                            getMvpView().onError(result.getError().getDisplayMessage());
                            getMvpView().showError();
                        }
                    }

                    @Override
                    public void onCompleted() {
//                        getMvpView().hideBathroomDialog(true);
                    }
                });
    }


    @Override
    public void precondition(boolean isShowDialog) {
        addObserver(bathroomDataManager.getOrderPrecondition(),new NetworkObserver<ApiResult<BathOrderPreconditionRespDTO>>(){
            @Override
            public void onStart() {
                if (isShowDialog) {
                    getMvpView().showBathroomDialog("正在加载");
                }
            }

            @Override
            public void onReady(ApiResult<BathOrderPreconditionRespDTO> result) {
                 if (result.getError() == null){

                     if (result.getData().getStatus() == INIT){
                         getMvpView().hideBathroomDialog(true);
                     }
                     getMvpView().saveBookingInfo(result.getData());
                     if (result.getData().getStatus() == QUEUEING){
                            getMvpView().startQueue(result.getData().getBathQueueId());
                            getMvpView().hideBathroomDialog(true);
                     }else if (result.getData().getStatus() == BOOKING_SUCCESS){
                         /**
                          * 进入预约流程 ,查询预约状态
                          */
                          queryBooking(result.getData().getBathBookingId());
                     }else if (result.getData().getStatus() == PRE_USING){
                         /**
                          * 进入用水流程，查询用水状态
                          */
                           queryBathorder(result.getData().getBathOrderId());
                     }
                 }else{
                     getMvpView().hideBathroomDialog(false);
                     getMvpView().onError(result.getError().getDisplayMessage());
                 }
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    @Override
    public boolean getBathroomPassword() {
       return  bathroomDataManager.getBathroomPassword();
    }

    @Override
    public void setIsResume(boolean isResume) {
        this.isResume = isResume ;
    }

    @Override
    public void buildingTraffic(long id) {
        SimpleReqDTO reqDTO = new SimpleReqDTO();
        reqDTO.setId(id);
        addObserver(bathroomDataManager.traffi(reqDTO) ,new NetworkObserver<ApiResult<BuildingTrafficDTO>>(){


            @Override
            public void onStart() {

            }

            @Override
            public void onReady(ApiResult<BuildingTrafficDTO> result) {
                if (result.getError() == null){
                    getMvpView().trafficInfo(result.getData().getFloors());
                }else{
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });

    }

    @Override
    public void booking(long deviceNo, long floorId) {
        BathBookingReqDTO reqDTO = new BathBookingReqDTO();
        if (floorId != 0){
            reqDTO.setFloorId(floorId);
            reqDTO.setType(BathTradeType.BOOKING_WITHOUT_DEVICE.getCode());
        }else{
            reqDTO.setDeviceNo(deviceNo);
            reqDTO.setType(BathTradeType.BOOKING_DEVICE.getCode());
        }

        addObserver(bathroomDataManager.booking(reqDTO) , new NetworkObserver<ApiResult<TryBookingResultRespDTO>>(){

            @Override
            public void onStart() {
                if (deviceNo > 0){
                    getMvpView().showBathroomDialog("系统君正在预约，请稍后");
                }else{
                    super.onStart();
                }
            }

            @Override
            public void onReady(ApiResult<TryBookingResultRespDTO> result) {
                if (null == result.getError()) {
                    if (result.getData().getQueueInfo() != null){
                         clearTime();
                         getMvpView().startQueue(result.getData().getQueueInfo().getId());
                    }else{
                        if (result.getData().getBookingInfo()!= null){
                            if (result.getData().getBookingInfo().getStatus() ==ACCEPTED){
                                getMvpView().startBooking(result.getData().getBookingInfo().getId());
                            }else if (result.getData().getBookingInfo().getStatus() == INIT){
                                queryBooking(result.getData().getBookingInfo().getId());
                            }
                        }
                    }
                } else {
                    getMvpView().hideBathroomDialog(false);
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }
        });
    }

    @Override
    public void clearTime() {
        time = 0 ;
    }

    @Override
    public void queryBooking(long bookingId) {
        BathBookingStatusReqDTO simpleReqDTO = new BathBookingStatusReqDTO();
        simpleReqDTO.setId(bookingId +"");
        addObserver(bathroomDataManager.query(simpleReqDTO) , new NetworkObserver<ApiResult<BathBookingRespDTO>>(){


            @Override
            public void onStart() {

            }

            @Override
            public void onReady(ApiResult<BathBookingRespDTO> result) {

                 if (result.getError() == null){
                       if (result.getData().getStatus() == ACCEPTED){
                           clearTime();
                           getMvpView().startBooking(result.getData().getId());
                       }else if (result.getData().getStatus() == OPENED){
                           getMvpView().startUsing(result.getData().getBathOrderId());

                       }else if (result.getData().getStatus() == INIT){
                           if (time < 5) {
                               delay(3, new Action1<Long>() {
                                   @Override
                                   public void call(Long aLong) {
                                       time++;
                                        queryBooking(bookingId);

                                   }
                               });
                           }else{
                                   getMvpView().hideBathroomDialog(false);
                                   clearTime();
                                   precondition(false);
                               getMvpView().onError("预约失败，请重试");
                           }
                       }else if (result.getData().getStatus() == FAIL){
                           getMvpView().hideBathroomDialog(false);
                           clearTime();
                           getMvpView().onError("预约失败，请重试");
                       }else{
                           getMvpView().hideBathroomDialog(false);
                           clearTime();
                       }
                 }else{
                     getMvpView().hideBathroomDialog(false);
                     clearTime();
                     getMvpView().onError(result.getError().getDisplayMessage());
                 }
            }
        });
    }

    @Override
    public void queryBathorder(long bathOrder) {
        SimpleReqDTO simpleReqDTO = new SimpleReqDTO();
        simpleReqDTO.setId(bathOrder);
        addObserver(bathroomDataManager.orderQuery(simpleReqDTO) , new NetworkObserver<ApiResult<BathOrderCurrentRespDTO>>(){

            @Override
            public void onReady(ApiResult<BathOrderCurrentRespDTO> result) {
                if (result.getError() == null){
                    if (result.getData().getStatus() == ORDER_USING){
                        getMvpView().hideBathroomDialog(true);
                        getMvpView().startUsing(result.getData().getId());
                    }else if (result.getData().getStatus() == ORDER_SETTLE){
                        getMvpView().hideBathroomDialog(true);
                        getMvpView().startOrderInfo(result.getData());
                    }
                }else{
                    getMvpView().onError(result.getError().getDisplayMessage());
                }
            }
        });
    }



    private void delay(int time ,  Action1<Long> action0 ){
        this.subscriptions.add(RxHelper.delay(time , action0));
    }
}
