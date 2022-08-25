const getWeekOfMonth = (dateFrom = new Date()) => {
    const currentDate = new Date(dateFrom)
    const day = dateFrom.getDate();
    const startOfMonth = new Date(currentDate.setDate(1));
    const weekDay = startOfMonth.getDay();
    return parseInt(String(((weekDay - 1) + day) / 7)) + 1;
}

const localDateTimeFormatter = (dateFrom = new Date()) => {
    const year = dateFrom.getFullYear();
    const month = ('0' + (dateFrom.getMonth() + 1)).slice(-2);
    const day = ('0' + dateFrom.getDate()).slice(-2);
    const hour = ('0' + dateFrom.getHours()).slice(-2);
    const minutes = ('0' + dateFrom.getMinutes()).slice(-2);

    return year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":00";
}

export {getWeekOfMonth, localDateTimeFormatter};