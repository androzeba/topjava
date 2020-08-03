// $(document).ready(function () {

function filter() {
    let startDate, endDate, startTime, endTime;
    startDate = $('#startDate').val();
    endDate = $('#endDate').val();
    startTime = $('#startTime').val();
    endTime = $('#endTime').val();
    // const url = "profile/meals";
    context.ajaxUrl = "profile/meals/filter?" +
        "startDate=" + startDate +
        "&endDate=" + endDate +
        "&startTime=" + startTime +
        "&endTime=" + endTime;
    $.ajax({
        url: context.ajaxUrl,
        type: "GET"
    }).done(function (data) {
        context.datatableApi.clear().rows.add(data).draw();
        context.ajaxUrl = "profile/meals/";
    });
}

function clearFilter() {
    context.ajaxUrl = "profile/meals";
    $("#startDate").val("");
    $("#endDate").val("");
    $("#startTime").val("");
    $("#endTime").val("");
    $.ajax({
        url: context.ajaxUrl,
        type: "GET"
    }).done(function (data) {
        context.datatableApi.clear().rows.add(data).draw();
    });
}

$(function () {
    makeEditable({
            ajaxUrl: "profile/meals/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "dateTime"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "calories"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            })
        }
    );
});