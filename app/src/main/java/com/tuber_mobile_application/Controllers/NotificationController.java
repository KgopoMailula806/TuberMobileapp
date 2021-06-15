package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationController
{
    /**
     *
     * @param id
     * @return
     */
    @GET("api/Notification/popUsersUnseenNotificationsMobileForIteration/{id}")
    Call<List<Notification>> popUsersUnseenNotificationsMobileForIteration(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Notification/popUsersUnseenNotificationsMobileForOnce/{id}")
    Call<List<Notification>> popUsersUnseenNotificationsMobileForOnce(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Notification/popUsersUnseenNotifications/{id}")
    Call<List<Notification>> getUnseenNotifications(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Notification/getNotification/{id}")
    Call<Notification> getNotification(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Notification/popAllUsersNotifications/{id}")
    Call<List<Notification>> getAllNotifications(@Path("id") int id);

    /**
     *
     * @param notificationID
     * @return
     */
    @GET("api/Notification/ChangeSeenStatus/{id}")
    Call<Integer> changeNotificationStatus(@Path("id") int notificationID);

    /**
     *
     * @param notificationID
     * @return
     */
    @GET("api/Notification/ChangeSeenStatusForPing/{id}")
    Call<Integer> ChangeSeenStatusForPing(@Path("id") int notificationID);

    /**
     *
     * @param userID
     * @return
     */
    @GET("api/Notification/getNumberOfUnreadNotifications/{userID}")
    Call<Integer> getNumberOfUnreadNotifications(@Path("userID") int userID);

    /**
     *
     * @param notification -
     * @return - the either the primary key of the notification or 0
     */
    @POST("api/Notification/pushnotification")
    Call<Integer> pushnotification(@Body Notification notification);
}
