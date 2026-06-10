export function formatDate(value) {
    if (!value) return "";
    return new Date(value).toLocaleString("es-PE");
}

export function formatMoney(value) {
    const number = Number(value || 0);
    return `S/ ${number.toFixed(2)}`;
}