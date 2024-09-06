const uploadField = document.getElementById("coverPhoto");
const bytesInMegabyte = 1048576;
const imageUpdateLimitMegabytes = 3;
const imageUploadLimitBytes = imageUpdateLimitMegabytes * bytesInMegabyte;

uploadField.onchange = function () {
    if (this.files[0].size > imageUploadLimitBytes) {
        alert("File is too big!");
        this.value = "";
    }
};
