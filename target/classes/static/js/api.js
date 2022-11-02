import axios from './axios.js';

const utils={
    Category : (url) => {
        axios.get(url).then((res) => {
            console.log(res.data)
            return res.data;
        }).catch((error) => {
            console.log("发生了错误")
            console.log(error)
        })
    }
}
export default utils;