// $(document).ready(function () {

$(function () {
    makeEditable({
            ajaxUrl: "admin/users/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "roles"
                    },
                    {
                        "data": "enabled"
                    },
                    {
                        "data": "registered"
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

function setUserStatus(elem) {
    var row = $(elem).closest('tr');
    var id = row.attr("id");
    var enabled;
    enabled = !!elem.checked;
    $.ajax({
        url: context.ajaxUrl + id,
        type: "POST",
        data: {enabled: enabled},
        success: function () {
            row.attr("data-enabled", enabled);
            enabled ? successNoty("Enabled") : successNoty("Disabled");
        },
        error: function () {
            $(elem).attr("checked", !enabled);
        }
    });
}

function updateCurrentTable() {
    updateTable();
}