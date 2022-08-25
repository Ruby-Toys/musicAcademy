const getWeekOfMonth = (dateFrom = new Date()) => {
    const currentDate = new Date(dateFrom)
    const day = dateFrom.getDate();
    const startOfMonth = new Date(currentDate.setDate(1));
    const weekDay = startOfMonth.getDay();
    return parseInt(String(((weekDay - 1) + day) / 7)) + 1;
}

export {getWeekOfMonth};