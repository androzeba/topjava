// $(document).ready(function () {

$('#filterButton').click(function () {
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
    updateTable();
});


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