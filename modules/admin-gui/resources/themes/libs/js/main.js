$(document).ready(function () {
    var selected = '';
    $("select").change(function () {
        $("option:selected").each(function () {
            selected = $(this).attr('value');
        });
    }).trigger("change");

    $('.btn-remove').click(function () {
        $('.mut').find("option[value=" + selected + "]").remove();
    });

    $('.btnaddnote').click(function () {
        var txtaddnote = $('#txtaddnote').val();

        if (txtaddnote != '') {
            if ($('option[value=' + txtaddnote + ']').length != 0) {
                alert("Mô tả đã tồn tại");
            } else {
                $('.mut').append("<option value=" + txtaddnote + ">" + txtaddnote + "</option>")
            }
        } else {
            alert("Chưa nhập mô tả.");
        }
    });



    // check active checkCbUseApproval
    checked = $("input[id='cbUseApproval']:checked").length;
    if (checked != 0) {
        $('#EmailAddress').attr("disabled", false);
        $('#ApprovalFromAddress').attr("disabled", false);
    } else {
        $('#EmailAddress').attr("disabled", true);
        $('#ApprovalFromAddress').attr("disabled", true);
    }



    // check active CbUseAutoEnrollment
    checked = $("input[id='CbUseAutoEnrollment']:checked").length;
    if (checked != 0) {
        $('#slAutoEnrollmentCA').attr("disabled", false);
        $('#CbUse').attr("disabled", false);
        $('#txtDcServer').attr("disabled", false);
        $('#txtDcServerPort').attr("disabled", false);
        $('#txtAdAccount').attr("disabled", false);
        $('#txtAdAccountPw').attr("disabled", false);
        $('#txtBaseDN').attr("disabled", false);
    } else {
        $('#slAutoEnrollmentCA').attr("disabled", true);
        $('#CbUse').attr("disabled", true);
        $('#txtDcServer').attr("disabled", true);
        $('#txtDcServerPort').attr("disabled", true);
        $('#txtAdAccount').attr("disabled", true);
        $('#txtAdAccountPw').attr("disabled", true);
        $('#txtBaseDN').attr("disabled", true);
    }

    // End Entity Profile
    if ($("#SlEndEntityProfile option:selected").attr("value") == 2) {
        $('.showInEmpty').css("display", "none");
    } else {
        $('.showInEmpty').css("display", "");
    }


    
    
    
    $('.checkAll').click(function () {
        $('.tbl-search input:checkbox').each(function () {
            $(this).prop('checked', true);
        })
    });
    $('.uncheckAll').click(function () {
        $('.tbl-search input:checkbox').each(function () {
            $(this).prop('checked', false);
        })
    });
    
    
    $('.btnSearchTbl').click(function(){
       $('.tbl-search').css("display","block"); 
    });


});


function checkSlEndEntityProfile() {
    if ($("#SlEndEntityProfile option:selected").attr("value") == 2) {
        $('.showInEmpty').css("display", "none");
    } else {
        $('.showInEmpty').css("display", "");
    }
}

function checkCbUseApproval() {
    if ($("input[id='cbUseApproval']:checked").length != 0) {
        $('#EmailAddress').attr("disabled", false);
        $('#ApprovalFromAddress').attr("disabled", false);
    } else {
        $('#EmailAddress').attr("disabled", true);
        $('#ApprovalFromAddress').attr("disabled", true);
    }
}

function CbUseAutoEnrollment() {
    if ($("input[id='CbUseAutoEnrollment']:checked").length != 0) {
        $('#slAutoEnrollmentCA').attr("disabled", false);
        $('#CbUse').attr("disabled", false);
        $('#txtDcServer').attr("disabled", false);
        $('#txtDcServerPort').attr("disabled", false);
        $('#txtAdAccount').attr("disabled", false);
        $('#txtAdAccountPw').attr("disabled", false);
        $('#txtBaseDN').attr("disabled", false);
    } else {
        $('#slAutoEnrollmentCA').attr("disabled", true);
        $('#CbUse').attr("disabled", true);
        $('#txtDcServer').attr("disabled", true);
        $('#txtDcServerPort').attr("disabled", true);
        $('#txtAdAccount').attr("disabled", true);
        $('#txtAdAccountPw').attr("disabled", true);
        $('#txtBaseDN').attr("disabled", true);
    }
}


// 

function btnSlide() {
    if ($('.slideBtn').attr("title") == "Basic") {
        $('.slideBtn').attr("title", "Advanced").html("Advanced");
        $('.tbl-advanced').hide();
        $('.tbl-basic').show();
    } else
    if ($('.slideBtn').attr("title") == "Advanced") {
        $('.slideBtn').attr("title", "Basic").html("Basic");
        $('.tbl-basic').hide();
        $('.tbl-advanced').show();
    }

};
