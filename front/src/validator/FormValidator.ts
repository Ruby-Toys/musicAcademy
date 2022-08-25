const nameRule = [
    {
        required: true,
        message: '이름을 입력해주세요.',
        trigger: 'blur',
    },
    {
        type: 'string',
        message: '이름 형식이 올바르지 않습니다.',
        trigger: ['blur', 'change'],
        pattern: /^[가-힣a-zA-Z\d]{2,20}$/
    },
]

const phoneNumberRule = [
    {
        required: true,
        message: '연락처를 입력해주세요.',
        trigger: 'blur',
    },
    {
        type: 'string',
        message: '연락처 형식이 올바르지 않습니다.',
        trigger: ['blur', 'change'],
        pattern: /^(010|011|016|017|019)\d{3,4}\d{4}$/
    },
]

const emailRule = [
    {
        required: true,
        message: '이메일을 입력해주세요.',
        trigger: 'blur',
    },
    {
        type: 'email',
        message: '이메일 형식이 올바르지 않습니다.',
        trigger: ['blur', 'change'],
    },
]

const courseRule = [{required: true}];

const gradeRule = [{required: true}];

export {nameRule, emailRule, phoneNumberRule, courseRule, gradeRule};