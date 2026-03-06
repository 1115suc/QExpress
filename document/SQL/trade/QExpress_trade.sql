create database qe_trade default character set utf8mb4 collate utf8mb4_general_ci;

create table qe_pay_channel
(
    id                   bigint        not null comment '主键'
        primary key,
    channel_name         varchar(32)   not null comment '通道名称',
    channel_label        varchar(32)   not null comment '通道唯一标记',
    domain               varchar(255)  not null comment '域名',
    app_id               varchar(32)   not null comment '商户appid',
    public_key           varchar(2000) not null comment '支付公钥',
    merchant_private_key varchar(2000) not null comment '商户私钥',
    other_config         varchar(1000) null comment '其他配置',
    encrypt_key          varchar(255)  null comment 'AES混淆密钥',
    remark               varchar(400)  null comment '说明',
    notify_url           varchar(255)  not null comment '回调地址',
    enable_flag          varchar(10)   not null comment '是否有效',
    enterprise_id        bigint        not null comment '商户ID【系统内部识别使用】',
    created              datetime      not null comment '创建时间',
    updated              datetime      not null comment '更新时间'
)
    collate = utf8mb4_bin;

create table qe_refund_record
(
    id               bigint                                 not null comment '主键'
        primary key,
    trading_order_no bigint                                 not null comment '交易系统订单号【对于三方来说：商户订单】',
    product_order_no bigint                                 not null comment '业务系统订单号',
    refund_no        bigint                                 not null comment '本次退款订单号',
    enterprise_id    bigint                                 not null comment '商户号',
    trading_channel  varchar(32) collate utf8mb4_general_ci not null comment '退款渠道【支付宝、微信、现金】',
    refund_status    int                                    not null comment '退款状态：1-退款中，2-成功, 3-失败',
    refund_code      varchar(80) charset utf8mb3            null comment '返回编码',
    refund_msg       text charset utf8mb3                   null comment '返回信息',
    memo             varchar(150) charset utf8mb3           not null comment '备注【订单门店，桌台信息】',
    refund_amount    decimal(12, 2)                         not null comment '本次退款金额',
    total            decimal(12, 2)                         not null comment '原订单金额',
    created          datetime                               not null comment '创建时间',
    updated          datetime                               not null comment '创建时间'
)
    comment '退款记录表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

create index created
    on qe_refund_record (created);

create index product_order_no
    on qe_refund_record (product_order_no);

create index refund_no
    on qe_refund_record (refund_no);

create index refund_status
    on qe_refund_record (refund_status);

create index trading_order_no
    on qe_refund_record (trading_order_no);

create table qe_trading
(
    id               bigint                                 not null comment '主键'
        primary key,
    product_order_no bigint                                 not null comment '业务系统订单号',
    trading_order_no bigint                                 not null comment '交易系统订单号【对于三方来说：商户订单】',
    trading_channel  varchar(32) collate utf8mb4_general_ci not null comment '支付渠道【支付宝、微信、现金、免单挂账】',
    trading_type     varchar(22)                            not null comment '交易类型【付款、退款、免单、挂账】',
    trading_state    int                                    not null comment '交易单状态【1-待付款,2-付款中,3-付款失败,4-已结算,5-取消订单,6-免单,7-挂账】',
    payee_name       varchar(50)                            null comment '收款人姓名',
    payee_id         bigint                                 null comment '收款人账户ID',
    payer_name       varchar(50)                            null comment '付款人姓名',
    payer_id         bigint                                 null comment '付款人Id',
    trading_amount   decimal(22, 2)                         not null comment '交易金额，单位：元',
    refund           decimal(12, 2) default 0.00            null comment '退款金额【付款后，单位：元',
    is_refund        varchar(32) collate utf8mb4_general_ci null comment '是否有退款：YES，NO',
    result_code      varchar(80)                            null comment '第三方交易返回编码【最终确认交易结果】',
    result_msg       varchar(255)                           null comment '第三方交易返回提示消息【最终确认交易信息】',
    result_json      varchar(2000)                          null comment '第三方交易返回信息json【分析交易最终信息】',
    place_order_code varchar(80)                            null comment '统一下单返回编码',
    place_order_msg  varchar(255)                           null comment '统一下单返回信息',
    place_order_json text                                   null comment '统一下单返回信息json【用于生产二维码、Android ios唤醒支付等】',
    enterprise_id    bigint                                 not null comment '商户号',
    memo             varchar(150)                           null comment '备注【订单门店，桌台信息】',
    qr_code          text                                   null comment '二维码base64数据',
    open_id          varchar(36) collate utf8mb4_unicode_ci null comment 'open_id标识',
    enable_flag      varchar(10)                            null comment '是否有效',
    updated          datetime                               not null comment '创建时间',
    created          datetime                               not null comment '创建时间'
)
    comment '交易订单表' charset = utf8mb3
                         row_format = DYNAMIC;

create index created
    on qe_trading (created);

create index enable_flag
    on qe_trading (enable_flag);

create index index_order_id
    on qe_trading (product_order_no);

create index index_tpptrs
    on qe_trading (trading_channel);

create index trading_order_no
    on qe_trading (trading_order_no);

create index trading_state
    on qe_trading (trading_state);

create table undo_log
(
    branch_id     bigint       not null comment 'branch transaction id',
    xid           varchar(128) not null comment 'global transaction id',
    context       varchar(128) not null comment 'undo_log context,such as serialization',
    rollback_info longblob     not null comment 'rollback info',
    log_status    int          not null comment '0:normal status,1:defense status',
    log_created   datetime(6)  not null comment 'create datetime',
    log_modified  datetime(6)  not null comment 'modify datetime',
    constraint ux_undo_log
        unique (xid, branch_id)
)
    comment 'AT transaction mode undo table' charset = utf8mb3
                                             row_format = DYNAMIC;

