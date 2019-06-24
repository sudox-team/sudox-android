#ifndef SSOCKETS_SOCKETCLIENT_H
#define SSOCKETS_SOCKETCLIENT_H

#include "SocketListener.h"
#include "structs/Packet.h"

#include <sys/epoll.h>
#include <cstdint>
#include <string>
#include <netinet/in.h>
#include <optional>
#include <deque>
#include <mutex>
#include <callbacks/SocketCallback.h>

static SocketListener listener;

class SocketClient {
private:
    std::mutex send_mutex;
    std::deque<Packet> sending_queue;
    epoll_event event_mask;
    std::string host;
    uint16_t port;
    int socket_fd = -1;
    int connected = false;

    std::optional<sockaddr_in> get_address();

    bool init_socket();

    bool link_with_listener();

public:
    SocketCallback *callback = nullptr;

    SocketClient(std::string host, uint16_t port);

    ~SocketClient();

    void connect();

    void close(bool error);

    bool opened();

    size_t available();

    size_t read(char *&buffer, size_t count);

    void send(char *buffer, size_t count, bool urgent);

    void adjust_write_flag();

    void handle_events(uint32_t events);

    bool check_errors();
};

#endif
